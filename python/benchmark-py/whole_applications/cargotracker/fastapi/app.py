from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: Full DDD app 25 KLOC; Python provides root+cargos REST DEGRADED
app = FastAPI()

@app.get("/cargo-tracker/index.xhtml")
async def handler():
    return HTMLResponse("<html><body><h1>cargotracker</h1></body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
