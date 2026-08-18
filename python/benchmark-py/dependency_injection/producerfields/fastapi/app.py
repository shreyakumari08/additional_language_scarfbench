from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: JPA todo list via in-memory list (Python) — DEGRADED
app = FastAPI()

@app.get("/producerfields")
async def handler():
    return HTMLResponse("<html><body><h1>To-Do List</h1><ul></ul></body></html>")


@app.get("/")
async def root():
    return PlainTextResponse("OK")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
