from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: 14 KLOC trader app; Python REST quotes/portfolio DEGRADED
app = FastAPI()

@app.get("/daytrader/")
async def handler():
    return HTMLResponse("<html><body><h1>daytrader</h1></body></html>")


@app.get("/")
async def root():
    return HTMLResponse("<html><body>OK</body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=9080)
