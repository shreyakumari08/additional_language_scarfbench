// DEGRADED: JSR-356 WS chat → HTTP UI
#[macro_use] extern crate rocket;


#[get("/")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>websocketbot</h1></body></html>")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket websocketbot on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_0])
        .launch()
        .await?;
    Ok(())
}
