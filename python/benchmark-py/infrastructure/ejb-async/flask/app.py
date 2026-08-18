from flask import Flask, jsonify, request, Response


# DEGRADED: EJB @Async → Python thread pool
app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "<html><body><h1>Async Mailer</h1></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=9080)
