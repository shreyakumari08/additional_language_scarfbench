from flask import Flask, jsonify, request, Response


# DEGRADED: JAX-RS CRUD; Python provides REST CRUD
app = Flask(__name__)

@app.route("/webapi", methods=["GET"])
def handler():
    return "<html><body><h1>jaxrs-customer</h1></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "<html><body>OK</body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
