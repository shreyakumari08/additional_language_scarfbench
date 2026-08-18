from flask import Flask, jsonify, request, Response


# DEGRADED: JPA 7-entity graph — Python uses in-memory dicts DEGRADED
app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "<html><body><h1>order</h1></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8081)
