# 📄 FinTafsir – PDF Reader API using Java + LLM (OpenAI)

FinTafsir is a Java-based Spring Boot application that reads uploaded PDF files (like bank statements or financial reports), extracts relevant information (e.g., name, email, opening balance, closing balance), and sends the content to a Large Language Model (LLM) like OpenAI GPT to retrieve structured data. The app is fully containerized using Docker and provides a clean, dark-themed frontend for uploading PDFs.

## 🚀 Features

- Upload a PDF and extract key data using LLM
- RESTful API built using Spring Boot
- PDF text extraction using Apache PDFBox
- GPT-based language processing via OpenAI API
- Structured JSON extraction with raw-text fallback
- Input validation (file type, size, extension)
- API key configured via environment variable (fail-fast on missing key)
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

## 🛠️ Getting Started

### Prerequisites

- JDK 17+
- Maven
- OpenAI API Key
- Docker (optional)

### 🔧 Local Run

```bash
# Set your OpenAI API key
export OPENAI_API_KEY=sk-your-key-here

# Build and run
mvn clean install
mvn spring-boot:run
```

The app will start at **http://localhost:64829**. Open `http://localhost:64829/index.html` for the upload UI.

> **Note:** The application will fail to start if `OPENAI_API_KEY` is not set.

---

### 🐳 Docker Setup

```bash
# Set the key in your shell, then run
export OPENAI_API_KEY=sk-your-key-here
docker-compose up --build
```

The `docker-compose.yml` passes `OPENAI_API_KEY` into the container automatically.

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
