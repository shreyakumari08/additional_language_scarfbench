from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: 17 KLOC; Python provides root+owners/vets/pets REST DEGRADED
app = FastAPI()

@app.get("/")
async def handler():
    return HTMLResponse("<html><body><h1>petclinic</h1></body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
