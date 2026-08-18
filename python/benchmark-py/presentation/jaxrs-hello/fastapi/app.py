from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse

app = FastAPI()

@app.get("/helloworld")
async def handler():
    return HTMLResponse("<html><body><h1>Hello</h1><p>Greetings!</p></body></html>")


@app.get("/")
async def root():
    return HTMLResponse("<html><body>Greetings!</body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
