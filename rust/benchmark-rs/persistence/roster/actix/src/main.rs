// DEGRADED: 5-entity multi-module JPA; Rust in-memory dicts
use actix_web::{App, HttpServer};


async fn h_0() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("<html><body><h1>roster</h1></body></html>")
}

async fn h_1() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("<html><body>OK</body></html>")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix roster on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/roster", actix_web::web::get().to(h_0))
            .route("/", actix_web::web::get().to(h_1))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
