from flask import Flask, jsonify, request, Response


# DEGRADED: EJB timer → threading.Timer
app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "<html><body><h1>Timer Session</h1><p>Last programmatic timeout: never</p><p>Last automatic timeout: never</p></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=9080)
