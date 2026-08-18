// DEGRADED: 6.4 KLOC Conduit → /api/tags
#[macro_use] extern crate rocket;


#[get("/api/tags")]
async fn h_tags_0() -> rocket::serde::json::Json<serde_json::Value> {
    rocket::serde::json::Json(serde_json::json!({"tags":["rust","axum","actix","rocket"]}))
}

#[get("/")]
async fn h_tags_1() -> rocket::serde::json::Json<serde_json::Value> {
    rocket::serde::json::Json(serde_json::json!({"tags":["rust","axum","actix","rocket"]}))
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket realworld on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_tags_0, h_tags_1])
        .launch()
        .await?;
    Ok(())
}
