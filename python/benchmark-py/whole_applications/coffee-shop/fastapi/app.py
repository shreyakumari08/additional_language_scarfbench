from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: 61 KLOC microservices; Python single-app menu+orders DEGRADED
app = FastAPI()

@app.get("/")
async def handler():
    return HTMLResponse("<html><body><h1>coffee-shop</h1></body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
