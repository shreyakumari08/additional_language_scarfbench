from flask import Flask, jsonify, request, Response

app = Flask(__name__)

@app.route("/response", methods=["GET", "POST"])
def handler():
    return "Hello, " + (request.form.get("name", "") or request.args.get("name", "world")).lower()


@app.route("/", methods=["GET"])
def root():
    return "OK"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
