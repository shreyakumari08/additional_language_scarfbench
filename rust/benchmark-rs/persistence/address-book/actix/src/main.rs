// FULL-fidelity
use actix_web::{App, HttpServer};


async fn h_empty() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().json(Vec::<serde_json::Value>::new())
}

async fn h_1() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("OK")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix address-book on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/contacts", actix_web::web::get().to(h_empty))
            .route("/", actix_web::web::get().to(h_1))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
