from flask import Flask, jsonify, request, Response

app = Flask(__name__)

@app.route("/greeting", methods=["GET"])
def handler():
    return "Hello, " + request.args.get("name", "World")


@app.route("/", methods=["GET"])
def root():
    return "Hello, World"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
