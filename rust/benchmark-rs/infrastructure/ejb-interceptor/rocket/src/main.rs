// FULL-fidelity
#[macro_use] extern crate rocket;

fn shift(s: &str) -> String {
    let cin: Vec<char> = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().collect();
    let cout: Vec<char> = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA".chars().collect();
    s.chars().map(|c| match cin.iter().position(|&x| x==c) { Some(i)=>cout[i], None=>c }).collect()
}


#[get("/response?<name>")]
async fn h_lower_get_0(name: Option<String>) -> rocket::response::content::RawHtml<String> {
    let n = name.unwrap_or_else(|| "world".to_string());
    rocket::response::content::RawHtml(format!("Hello, {}", n.to_lowercase()))
}

#[post("/response?<name>", data = "<form>")]
async fn h_lower_post_1(name: Option<String>, form: Option<rocket::form::Form<std::collections::HashMap<String,String>>>) -> rocket::response::content::RawHtml<String> {
    let n = form.as_ref().and_then(|f| f.get("name").cloned()).or(name).unwrap_or_else(|| "world".to_string());
    rocket::response::content::RawHtml(format!("Hello, {}", n.to_lowercase()))
}

#[get("/")]
async fn h_2() -> rocket::response::content::RawHtml<&'static str> {
    rocket::response::content::RawHtml("OK")
}


#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let config = rocket::Config::figment()
        .merge(("address", "0.0.0.0"))
        .merge(("port", 8080u16));
    println!("Rocket ejb-interceptor on 8080");
    rocket::custom(config)
        
        .mount("/", routes![h_lower_get_0, h_lower_post_1, h_2])
        .launch()
        .await?;
    Ok(())
}
