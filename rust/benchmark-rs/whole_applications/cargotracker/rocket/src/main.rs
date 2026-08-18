// DEGRADED: 25 KLOC DDD → root subset
#[macro_use] extern crate rocket;


#[get("/cargo-tracker/index.xhtml")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>cargotracker</h1></body></html>")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket cargotracker on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_0])
        .launch()
        .await?;
    Ok(())
}
