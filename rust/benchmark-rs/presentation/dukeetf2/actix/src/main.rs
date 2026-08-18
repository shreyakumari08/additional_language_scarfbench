// DEGRADED: JSR-356 WebSocket → HTTP polling
use actix_web::{App, HttpServer};


async fn h_tick() -> impl actix_web::Responder {
    use rand::Rng;
    let mut rng = rand::thread_rng();
    let tick: f64 = rng.gen_range(99.0..101.0);
    let vol: u64 = rng.gen_range(100000..999999);
    actix_web::HttpResponse::Ok().content_type("text/plain; charset=utf-8").body(format!("Current tick: {:.2} / {}", tick, vol))
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    
    println!("Actix dukeetf2 on 8080");
    HttpServer::new(move || {
        App::new()
            
            .route("/", actix_web::web::get().to(h_tick))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
