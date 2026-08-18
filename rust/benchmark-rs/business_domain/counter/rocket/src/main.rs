// FULL-fidelity
#[macro_use] extern crate rocket;


#[get("/counter")]
async fn h_counter(counter: &rocket::State<std::sync::atomic::AtomicU64>) -> (rocket::http::ContentType, String) {
    let n = counter.fetch_add(1, std::sync::atomic::Ordering::SeqCst) + 1;
    (rocket::http::ContentType::Plain, format!("accessed {} time(s)", n))
}

#[get("/")]
async fn h_1() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("OK")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket counter on 8080");
    rocket::custom(config)
        .manage(std::sync::atomic::AtomicU64::new(0))
        .mount("/", routes![h_counter, h_1])
        .launch()
        .await?;
    Ok(())
}
