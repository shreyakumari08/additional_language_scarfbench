// DEGRADED: EJB @Schedule → tokio::time::interval (no persistence)
#[macro_use] extern crate rocket;


#[get("/")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>Timer Session</h1><p>Last programmatic timeout: never</p><p>Last automatic timeout: never</p></body></html>")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 9080u16));
    println!("Rocket ejb-timersession on 9080");
    rocket::custom(config)
        
        .mount("/", routes![h_0])
        .launch()
        .await?;
    Ok(())
}
