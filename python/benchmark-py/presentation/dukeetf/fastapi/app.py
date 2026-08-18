from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse

app = FastAPI()

@app.get("/")
async def handler():
    import random
    return PlainTextResponse(f"Current tick: {random.uniform(99, 101):.2f} / {random.randint(100000, 999999)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
