// DEGRADED: 17 KLOC Spring → root subset
use actix_web::{App, HttpServer};


async fn h_0() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("<html><body><h1>petclinic</h1></body></html>")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix petclinic on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/", actix_web::web::get().to(h_0))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
