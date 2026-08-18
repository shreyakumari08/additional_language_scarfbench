from flask import Flask, jsonify, request, Response

app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "Ready"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=9080)
