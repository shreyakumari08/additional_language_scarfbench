from flask import Flask, jsonify, request, Response


# DEGRADED: JPA todo list via in-memory list (Python) — DEGRADED
app = Flask(__name__)

@app.route("/producerfields", methods=["GET"])
def handler():
    return "<html><body><h1>To-Do List</h1><ul></ul></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "OK"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
