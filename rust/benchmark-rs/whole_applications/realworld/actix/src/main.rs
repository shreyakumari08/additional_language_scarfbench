// DEGRADED: 6.4 KLOC Conduit → /api/tags
use actix_web::{App, HttpServer};


async fn h_tags_0() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().json(serde_json::json!({"tags":["rust","axum","actix","rocket"]}))
}

async fn h_tags_1() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().json(serde_json::json!({"tags":["rust","axum","actix","rocket"]}))
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix realworld on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/api/tags", actix_web::web::get().to(h_tags_0))
            .route("/", actix_web::web::get().to(h_tags_1))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
