from flask import Flask, jsonify, request, Response


# DEGRADED: 14 KLOC trader app; Python REST quotes/portfolio DEGRADED
app = Flask(__name__)

@app.route("/daytrader/", methods=["GET"])
def handler():
    return "<html><body><h1>daytrader</h1></body></html>"


@app.route("/", methods=["GET"])
def root():
    return "<html><body>OK</body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=9080)
