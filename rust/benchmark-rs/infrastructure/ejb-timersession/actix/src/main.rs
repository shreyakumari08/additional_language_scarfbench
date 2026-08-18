// DEGRADED: EJB @Schedule → tokio::time::interval (no persistence)
use actix_web::{App, HttpServer};


async fn h_0() -> impl actix_web::Responder {
    actix_web::HttpResponse::Ok().content_type("text/html; charset=utf-8").body("<html><body><h1>Timer Session</h1><p>Last programmatic timeout: never</p><p>Last automatic timeout: never</p></body></html>")
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix ejb-timersession on 9080");
    HttpServer::new(move || {
        App::new()
            
            .route("/", actix_web::web::get().to(h_0))
    })
    .bind("0.0.0.0:9080")?
    .run()
    .await
}
