from flask import Flask, jsonify, request, Response


# DEGRADED: 17 KLOC; Python provides root+owners/vets/pets REST DEGRADED
app = Flask(__name__)

@app.route("/", methods=["GET"])
def handler():
    return "<html><body><h1>petclinic</h1></body></html>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
