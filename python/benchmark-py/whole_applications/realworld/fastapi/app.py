from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: 6.4 KLOC conduit; Python provides /api/tags
app = FastAPI()

@app.get("/api/tags")
async def handler():
    return {"tags": ["python","flask","fastapi","django"]}


@app.get("/")
async def root():
    return {"tags":["python","flask","fastapi","django"]}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
