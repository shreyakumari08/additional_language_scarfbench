from fastapi import FastAPI, Request
from fastapi.responses import PlainTextResponse

app = FastAPI()

CIPHER_IN  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
CIPHER_OUT = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA"

async def _read_input(request: Request) -> str:
    q = request.query_params.get("inputString")
    if q is not None:
        return q
    ct = (request.headers.get("content-type") or "").lower()
    if ct.startswith("application/x-www-form-urlencoded") or ct.startswith("multipart/form-data"):
        try:
            form = await request.form()
            return form.get("inputString", "") or ""
        except Exception:
            return ""
    return ""


@app.get("/producermethods")
@app.post("/producermethods")
async def handler(request: Request):
    s = await _read_input(request)
    return PlainTextResponse("Coded: " + s.translate(str.maketrans(CIPHER_IN, CIPHER_OUT)))


@app.get("/")
async def root():
    return PlainTextResponse("OK")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8080)
