import os
import time
import logging
from typing import Optional, Dict, Any

from groq import Groq
from dotenv import load_dotenv

# Load env variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("groq_client")


class GroqClient:
    def __init__(self):
        api_key = os.getenv("GROQ_API_KEY")
        if not api_key:
            raise ValueError("Missing GROQ_API_KEY in environment variables")

        self.client = Groq(api_key=api_key)
        self.model = "llama-3.1-8b-instant"

    def generate(
        self,
        prompt: str,
        temperature: float = 0.7,
        max_tokens: int = 300,
        retries: int = 3
    ) -> Optional[Dict[str, Any]]:
        """
        Calls Groq API with retry + backoff and returns structured response
        """

        for attempt in range(1, retries + 1):
            try:
                start_time = time.time()

                response = self.client.chat.completions.create(
                    model=self.model,
                    messages=[
                        {"role": "system", "content": "You are a helpful AI assistant."},
                        {"role": "user", "content": prompt}
                    ],
                    temperature=temperature,
                    max_tokens=max_tokens
                )

                duration = round(time.time() - start_time, 2)

                content = response.choices[0].message.content

                logger.info(f"[Groq] Success in {duration}s (attempt {attempt})")

                return {
                    "content": content,
                    "model": self.model,
                    "response_time": duration,
                    "attempt": attempt
                }

            except Exception as e:
                wait_time = 2 ** attempt  # exponential backoff
                logger.error(f"[Groq] Attempt {attempt} failed: {str(e)}")

                if attempt < retries:
                    logger.info(f"[Groq] Retrying in {wait_time}s...")
                    time.sleep(wait_time)
                else:
                    logger.error("[Groq] All retry attempts failed")

        return None