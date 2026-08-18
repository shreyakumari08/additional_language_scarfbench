// DEGRADED: 7-entity JPA aggregate; Rust in-memory dict graph
#[macro_use] extern crate rocket;


#[get("/")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>order</h1></body></html>")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8081u16));
    println!("Rocket order on 8081");
    rocket::custom(config)
        
        .mount("/", routes![h_0])
        .launch()
        .await?;
    Ok(())
}
