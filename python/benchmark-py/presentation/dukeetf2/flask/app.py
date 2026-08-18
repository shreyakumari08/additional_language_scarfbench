from flask import Flask, jsonify, request, Response


# DEGRADED: WebSocket in original; Python provides HTTP fallback DEGRADED
app = Flask(__name__)

@app.route("/", methods=["GET", "POST"])
def handler():
    import random
    return f"Current tick: {random.uniform(99, 101):.2f} / {random.randint(100000, 999999)}"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
