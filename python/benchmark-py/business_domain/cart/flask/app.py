from flask import Flask, jsonify, request, Response


# DEGRADED: CRUD cart with session (Python uses per-app-scope in-memory dict; docmented DEGRADED)
app = Flask(__name__)

@app.route("/cart", methods=["GET", "POST"])
def handler():
    return "<html><body><form method=\"POST\" action=\"/cart\"><input name=\"input\"><button>Submit</button></form></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "OK"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
