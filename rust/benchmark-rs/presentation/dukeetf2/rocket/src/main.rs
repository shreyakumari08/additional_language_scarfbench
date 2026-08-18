// DEGRADED: JSR-356 WebSocket → HTTP polling
#[macro_use] extern crate rocket;


#[get("/")]
async fn h_tick_0() -> (rocket::http::ContentType, String) {
    use rand::Rng;
    let mut rng = rand::thread_rng();
    let tick: f64 = rng.gen_range(99.0..101.0);
    let vol: u64 = rng.gen_range(100000..999999);
    (rocket::http::ContentType::Plain, format!("Current tick: {:.2} / {}", tick, vol))
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket dukeetf2 on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_tick_0])
        .launch()
        .await?;
    Ok(())
}
