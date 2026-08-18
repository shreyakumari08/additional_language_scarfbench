from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse

app = FastAPI()

@app.get("/converter")
async def handler():
    return HTMLResponse("<html><body><form method=\"POST\" action=\"/converter\"><input name=\"input\"><button>Submit</button></form></body></html>")


@app.get("/")
async def root():
    return PlainTextResponse("OK")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
