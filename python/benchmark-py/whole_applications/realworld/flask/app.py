from flask import Flask, jsonify, request, Response


# DEGRADED: 6.4 KLOC conduit; Python provides /api/tags
app = Flask(__name__)

@app.route("/api/tags", methods=["GET"])
def handler():
    return jsonify({"tags": ["python","flask","fastapi","django"]})


@app.route("/", methods=["GET"])
def root():
    return '{"tags":["python","flask","fastapi","django"]}'

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
