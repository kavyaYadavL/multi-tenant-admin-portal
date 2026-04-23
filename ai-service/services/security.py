import re
from flask import request, jsonify


def strip_html(text: str) -> str:
    """Remove HTML tags from input"""
    return re.sub(r"<.*?>", "", text)


def detect_prompt_injection(text: str) -> bool:
    """Basic detection of prompt injection patterns"""
    suspicious_patterns = [
        "ignore previous instructions",
        "system prompt",
        "act as",
        "jailbreak",
        "override",
        "bypass"
    ]

    text_lower = text.lower()
    return any(pattern in text_lower for pattern in suspicious_patterns)


def validate_input():
    """Middleware to validate incoming request"""
    data = request.get_json()

    if not data or "input" not in data:
        return jsonify({"error": "Invalid input"}), 400

    user_input = data["input"]

    # Strip HTML
    clean_input = strip_html(user_input)

    # Detect injection
    if detect_prompt_injection(clean_input):
        return jsonify({"error": "Prompt injection detected"}), 400

    # Replace cleaned input back
    data["input"] = clean_input