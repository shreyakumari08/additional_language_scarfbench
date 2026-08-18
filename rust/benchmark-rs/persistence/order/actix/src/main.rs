// DEGRADED: 7-entity JPA aggregate; Rust in-memory dict graph
use actix_web::{App, HttpServer};


async fn h_0() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("<html><body><h1>order</h1></body></html>")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix order on 8081");
    HttpServer::new(move || {
        App::new()
            
            .route("/", actix_web::web::get().to(h_0))
    })
    .bind("0.0.0.0:8081")?
    .run()
    .await
}
