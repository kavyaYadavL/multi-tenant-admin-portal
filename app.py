from flask import Flask, request, jsonify
import time
from datetime import datetime
import os
from groq import Groq
from dotenv import load_dotenv

app = Flask(__name__)

# =========================
# LOAD ENV + GROQ CLIENT
# =========================
load_dotenv()
client = Groq(api_key=os.getenv("GROQ_API_KEY"))

# =========================
# DAY 2 PROMPT (REUSED)
# =========================
PRIMARY_PROMPT = """
You are CampusPe AI, a student-friendly text analysis assistant.

Your job is to analyze user input and respond in a structured, simple, and helpful way.

Rules:
- Understand the input carefully
- Provide clear explanation or analysis
- Keep responses short and structured
- If input is unclear, ask a clarification question
- Do not hallucinate or assume facts

Output format:
- Summary
- Key insights
- Sentiment (if applicable)

User Input:
{text}

Response:
"""

# =========================
# ROUTES
# =========================

@app.route("/")
def home():
    return {"message": "AI Service Running"}

@app.route("/health", methods=["GET"])
def health():
    return {
        "status": "healthy",
        "message": "AI Service is running"
    }

# =========================
# DAY 3: DESCRIBE ENDPOINT
# =========================
@app.route("/describe", methods=["POST"])
def describe():
    start_time = time.time()

    try:
        data = request.get_json()

        # ✅ Input validation
        if not data or "input" not in data:
            return jsonify({
                "status": "error",
                "message": "Please provide 'input' in request body"
            }), 400

        user_input = data["input"]

        # ✅ Load prompt
        prompt = PRIMARY_PROMPT.format(text=user_input)

        # ✅ Call Groq API (UPDATED MODEL)
        response = client.chat.completions.create(
            model="llama-3.1-8b-instant",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )

        ai_output = response.choices[0].message.content

        response_time = round((time.time() - start_time) * 1000, 2)

        # ✅ Final structured response
        return jsonify({
            "status": "success",
            "input": user_input,
            "output": ai_output,
            "generated_at": datetime.utcnow().isoformat(),
            "meta": {
                "response_time_ms": response_time,
                "model_used": "llama-3.1-8b-instant"
            }
        })

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": "Something went wrong",
            "error": str(e)
        }), 500


# =========================
# RUN APP
# =========================
if __name__ == "__main__":
    app.run(port=5000, debug=True)