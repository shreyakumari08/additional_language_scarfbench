// FULL-fidelity
use actix_web::{App, HttpServer};

fn shift(s: &str) -> String {
    let cin: Vec<char> = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().collect();
    let cout: Vec<char> = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA".chars().collect();
    s.chars().map(|c| match cin.iter().position(|&x| x==c) { Some(i)=>cout[i], None=>c }).collect()
}


async fn h_cipher_get(q: actix_web::web::Query<std::collections::HashMap<String,String>>) -> impl actix_web::Responder {
    let s = q.get("inputString").cloned().unwrap_or_default();
    actix_web::HttpResponse::Ok().content_type("text/plain; charset=utf-8").body(format!("Coded: {}", shift(&s)))
}

async fn h_cipher_post(q: actix_web::web::Query<std::collections::HashMap<String,String>>, form: Option<actix_web::web::Form<std::collections::HashMap<String,String>>>) -> impl actix_web::Responder {
    let s = form.as_ref().and_then(|f| f.get("inputString").cloned()).or_else(|| q.get("inputString").cloned()).unwrap_or_default();
    actix_web::HttpResponse::Ok().content_type("text/plain; charset=utf-8").body(format!("Coded: {}", shift(&s)))
}

async fn h_2() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("OK")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix decorators on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/decorators", actix_web::web::get().to(h_cipher_get))
            .route("/decorators", actix_web::web::post().to(h_cipher_post))
            .route("/", actix_web::web::get().to(h_2))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
