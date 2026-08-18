from flask import Flask, jsonify, request, Response


# DEGRADED: Full DDD app 25 KLOC; Python provides root+cargos REST DEGRADED
app = Flask(__name__)

@app.route("/cargo-tracker/index.xhtml", methods=["GET"])
def handler():
    return "<html><body><h1>cargotracker</h1></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
