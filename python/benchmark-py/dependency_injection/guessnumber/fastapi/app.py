from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: Original had session-scope game state; Python uses app-scope — DEGRADED
app = FastAPI()

@app.get("/guessnumber")
async def handler():
    return HTMLResponse("<html><body><form method=\"POST\" action=\"/guessnumber\"><input name=\"input\"><button>Submit</button></form></body></html>")


@app.get("/")
async def root():
    return PlainTextResponse("OK")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
