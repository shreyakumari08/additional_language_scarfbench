// FULL-fidelity
use actix_web::{App, HttpServer};

fn shift(s: &str) -> String {
    let cin: Vec<char> = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().collect();
    let cout: Vec<char> = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA".chars().collect();
    s.chars().map(|c| match cin.iter().position(|&x| x==c) { Some(i)=>cout[i], None=>c }).collect()
}


async fn h_lower_get(q: actix_web::web::Query<std::collections::HashMap<String,String>>) -> impl actix_web::Responder {
    let n = q.get("name").cloned().unwrap_or_else(|| "world".to_string());
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body(format!("Hello, {}", n.to_lowercase()))
}

async fn h_lower_post(q: actix_web::web::Query<std::collections::HashMap<String,String>>, form: Option<actix_web::web::Form<std::collections::HashMap<String,String>>>) -> impl actix_web::Responder {
    let n = form.as_ref().and_then(|f| f.get("name").cloned()).or_else(|| q.get("name").cloned()).unwrap_or_else(|| "world".to_string());
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body(format!("Hello, {}", n.to_lowercase()))
}

async fn h_2() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("OK")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix ejb-interceptor on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/response", actix_web::web::get().to(h_lower_get))
            .route("/response", actix_web::web::post().to(h_lower_post))
            .route("/", actix_web::web::get().to(h_2))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
