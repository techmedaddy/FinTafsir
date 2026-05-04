# 📄 FinTafsir – PDF Reader API using Java + LLM (OpenAI)

FinTafsir is a Java-based Spring Boot application that reads uploaded PDF files (like bank statements or financial reports), extracts relevant information (e.g., name, email, opening balance, closing balance), and sends the content to a Large Language Model (LLM) like OpenAI GPT to retrieve structured data. The app is fully containerized using Docker and provides a clean, dark-themed frontend for uploading PDFs.

## 🚀 Features

- Upload a PDF and extract key data using LLM
- RESTful API built using Spring Boot
- PDF text extraction using Apache PDFBox
- GPT-based language processing via OpenAI API
- Structured JSON extraction with raw-text fallback
- Input validation (file type, size, extension)
- API key configured via environment variable
- Dummy mode when API key is missing (for UI testing)
- Minimal frontend in dusky dark theme
- Dockerized with `Dockerfile` and `docker-compose.yml`

## System Design

![WhatsApp Image 2025-04-05 at 00 16 47_55fbff88](https://github.com/user-attachments/assets/002ccda8-8e81-4508-a678-a060b7574573)



## 🏗️ Project Structure

```bash
FinTafsir/
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── fintafsir/
│   │   │           ├── controller/
│   │   │           │   └── PdfController.java
│   │   │           ├── service/
│   │   │           │   └── PdfParserService.java
│   │   │           ├── model/
│   │   │           │   └── PdfResponse.java
│   │   │           └── FinTafsirApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── static/
│   │   │       ├── index.html
│   │   │       ├── style.css
│   │   │       └── script.js

```


## ⚙️ How It Works

1. A user uploads a PDF through the frontend or API.
2. The backend validates the file (type, size, extension).
3. The text is extracted using **Apache PDFBox**.
4. The text is sent to **OpenAI's GPT API** via OkHttp client with a JSON-only prompt.
5. The LLM responds with structured JSON containing:
   - Name  
   - Email  
   - Opening Balance  
   - Closing Balance  
6. The backend parses the JSON into `PdfResponse` fields. If parsing fails, the raw LLM output is returned as `rawText`.



## 🌐 API Endpoint

### `POST /api/parse-pdf`

**Request:**

- Content-Type: `multipart/form-data`
- Parameter: `file` → PDF file (max 10 MB)

**Validation Rules:**

| Check | HTTP Status |
|---|---|
| Empty / missing file | 400 Bad Request |
| Non-PDF content type | 400 Bad Request |
| Missing `.pdf` extension | 400 Bad Request |
| File > 10 MB | 413 Payload Too Large |

**Success Response:**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "openingBalance": "$5,000",
  "closingBalance": "$7,250",
  "rawText": "{ ... original LLM output ... }"
}
```

**Error Response:**

```json
{
  "error": "Only PDF files are accepted. Received content type: text/plain"
}
```

---

## 🛠️ Getting Started (Step‑by‑Step)

### ✅ Prerequisites

Make sure you have **all** of these installed:

1. **Java 17+**
2. **Maven 3.8+**
3. **Docker Desktop** (only if you want to run with Docker)
4. **OpenAI API Key** (optional — only required for real LLM extraction)

You can still run the app **without** an OpenAI key in **dummy mode** to test the UI.

---

## 🔑 OpenAI API Key Setup (Optional but Recommended)

1. Go to the OpenAI dashboard and create an API key.
2. In your terminal, export the key:

```bash
export OPENAI_API_KEY=sk-your-key-here
```

That’s it. The app will read the key from the environment.

If you **don’t** set a key, the app still runs but returns **dummy data**.

---

## ▶️ Run Locally (Without Docker)

From the project root:

```bash
mvn clean package
mvn spring-boot:run
```

Open in your browser:

- **Frontend UI:** http://localhost:64829/index.html
- **Backend API:** http://localhost:64829/api

---

## 🐳 Run with Docker

From the project root:

```bash
docker compose up -d --build
```

If you want real extraction, make sure you export the key **before** running Docker:

```bash
export OPENAI_API_KEY=sk-your-key-here
docker compose up -d --build
```

---

## ✅ Dummy Mode (No Key)

If `OPENAI_API_KEY` is **not** set:

- The app still starts
- The UI still works
- Responses return **fake demo data** so you can test the flow

---

## 💡 Frontend Preview

- Static HTML, CSS, JS in `src/main/resources/static`
- Accessible at: `http://localhost:64829/index.html`
- Upload a PDF and get results displayed in `<pre>` tag

## 🔐 Security Notes (For Production)

- Validate MIME type (`application/pdf`) ✅ (implemented)
- Limit max file size in `application.properties` ✅ (implemented — 10 MB)
- Add API authentication / rate-limiting
- Sanitize extracted text before LLM usage


## 🧠 LLM Prompt Sample

```text
Extract the following fields from this bank statement / financial PDF text
and respond with ONLY a valid JSON object, no extra text:
{
  "name": "<account holder name>",
  "email": "<email address or null>",
  "openingBalance": "<opening balance>",
  "closingBalance": "<closing balance>"
}

If a field is not found, use null.

PDF Text:
[PDF Content Here...]
```
