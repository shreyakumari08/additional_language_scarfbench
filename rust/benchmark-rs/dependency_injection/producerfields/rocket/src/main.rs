// DEGRADED: JPA @Produces todo list; Rust uses in-memory Vec
#[macro_use] extern crate rocket;


#[get("/producerfields")]
async fn h_0() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("<html><body><h1>To-Do List</h1><ul></ul></body></html>")
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
    println!("Rocket producerfields on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_0, h_1])
        .launch()
        .await?;
    Ok(())
}
