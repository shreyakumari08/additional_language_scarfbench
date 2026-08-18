from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, PlainTextResponse


# DEGRADED: RSVP tracker; Python in-memory events/persons
app = FastAPI()

@app.get("/webapi")
async def handler():
    return HTMLResponse("<html><body><h1>jaxrs-rsvp</h1></body></html>")


@app.get("/")
async def root():
    return HTMLResponse("<html><body>OK</body></html>")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
