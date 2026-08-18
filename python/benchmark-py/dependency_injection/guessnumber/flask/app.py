from flask import Flask, jsonify, request, Response


# DEGRADED: Original had session-scope game state; Python uses app-scope — DEGRADED
app = Flask(__name__)

@app.route("/guessnumber", methods=["GET", "POST"])
def handler():
    return "<html><body><form method=\"POST\" action=\"/guessnumber\"><input name=\"input\"><button>Submit</button></form></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "OK"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
