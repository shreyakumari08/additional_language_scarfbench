// FULL-fidelity
use actix_web::{App, HttpServer};

fn shift(s: &str) -> String {
    let cin: Vec<char> = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().collect();
    let cout: Vec<char> = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA".chars().collect();
    s.chars().map(|c| match cin.iter().position(|&x| x==c) { Some(i)=>cout[i], None=>c }).collect()
}


async fn h_greeting(q: actix_web::web::Query<std::collections::HashMap<String,String>>) -> impl actix_web::Responder {
    let n = q.get("name").cloned().unwrap_or_else(|| "World".to_string());
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body(format!("Hello, {}", n))
}

async fn h_1() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("Hello, World")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix hello-servlet on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/greeting", actix_web::web::get().to(h_greeting))
            .route("/", actix_web::web::get().to(h_1))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
