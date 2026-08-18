from flask import Flask, jsonify, request, Response


# DEGRADED: Original was SOAP; Python has no first-class SOAP — DEGRADED to REST GET
app = Flask(__name__)

@app.route("/helloservice", methods=["GET"])
def handler():
    return "<html><body><h1>Hello</h1><p>Greetings!</p></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "<html><body>Greetings!</body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
