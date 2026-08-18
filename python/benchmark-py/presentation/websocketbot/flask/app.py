from flask import Flask, jsonify, request, Response


# DEGRADED: WebSocket chat; Python provides HTTP UI + WS route DEGRADED
app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "<html><body><h1>websocketbot</h1></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
