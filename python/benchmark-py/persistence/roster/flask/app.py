from flask import Flask, jsonify, request, Response


# DEGRADED: JPA 5-entity multi-module — Python simplified DEGRADED
app = Flask(__name__)

@app.route("/roster", methods=["GET"])
def handler():
    return "<html><body><h1>roster</h1></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "<html><body>OK</body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
