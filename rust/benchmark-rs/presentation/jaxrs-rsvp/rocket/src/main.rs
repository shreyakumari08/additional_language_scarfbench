// DEGRADED: JAX-RS+JPA event tracker; Rust in-memory
#[macro_use] extern crate rocket;


#[get("/webapi")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>jaxrs-rsvp</h1></body></html>")
}

#[get("/")]
async fn h_1() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body>OK</body></html>")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket jaxrs-rsvp on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_0, h_1])
        .launch()
        .await?;
    Ok(())
}
