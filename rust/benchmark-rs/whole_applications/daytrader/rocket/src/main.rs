// DEGRADED: 14 KLOC EJB → REST subset
#[macro_use] extern crate rocket;

#[get("/")]
async fn h_daytrader() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>daytrader</h1></body></html>")
}

#[get("/")]
async fn h_root() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body>OK</body></html>")
}

#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 9080u16));
    println!("Rocket daytrader on 9080");
    rocket::custom(config)
        .mount("/daytrader", routes![h_daytrader])
        .mount("/", routes![h_root])
        .launch()
        .await?;
    Ok(())
}
