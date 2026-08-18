from flask import Flask, jsonify, request, Response

app = Flask(__name__)

@app.route("/billpayment", methods=["GET", "POST"])
def handler():
    return "<html><body><form method=\"POST\" action=\"/billpayment\"><input name=\"input\"><button>Submit</button></form></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "OK"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
