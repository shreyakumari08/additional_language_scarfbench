from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse

app = FastAPI()

@app.get("/greeting")
async def handler(name: str = "World"):
    return PlainTextResponse(f"Hello, {name}")


@app.get("/")
async def root():
    return PlainTextResponse("Hello, World")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
