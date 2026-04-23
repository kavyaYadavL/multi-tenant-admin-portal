from flask import Flask, request
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

from services.security import validate_input

app = Flask(__name__)

limiter = Limiter(
    key_func=get_remote_address,
    app=app,
    default_limits=["30 per minute"]
)


@app.before_request
def before_request():
    if request.method == "POST":
        return validate_input()


@app.route("/health", methods=["GET", "POST"])
def health():
    return {"status": "ok"}


if __name__ == "__main__":
    app.run(port=5000, debug=True)