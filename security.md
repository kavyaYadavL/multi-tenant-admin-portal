# SECURITY REVIEW — AI SERVICE

## Overview
This document outlines key security considerations and mitigation strategies for the AI service using Groq API.

---

## 1. Prompt Injection
**Threat:** Malicious input may manipulate AI behavior.

**Mitigation:**
- Input sanitization (strip HTML, scripts)
- Restrict prompt structure
- Validate input length and content

---

## 2. API Key Exposure
**Threat:** Leaking GROQ_API_KEY can allow unauthorized usage.

**Mitigation:**
- Store keys in `.env`
- Never commit `.env` to GitHub
- Rotate keys if exposed

---

## 3. Rate Limiting Abuse
**Threat:** Attackers may overload API with requests.

**Mitigation:**
- Use `flask-limiter` (30 req/min)
- Monitor traffic patterns

---

## 4. Sensitive Data Leakage
**Threat:** Users may send personal or confidential data.

**Mitigation:**
- Avoid sending PII in prompts
- Add validation to reject sensitive inputs

---

## 5. AI Response Manipulation
**Threat:** AI may generate unsafe or misleading content.

**Mitigation:**
- Use controlled system prompts
- Add output validation layer
- Fallback response if AI fails

---

## Summary
All major AI-related risks are identified and mitigation strategies are implemented or planned for upcoming tasks.