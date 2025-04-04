# 📄 FinTafsir – PDF Reader API using Java + LLM (OpenAI)

FinTafsir is a Java-based Spring Boot application that reads uploaded PDF files (like bank statements or financial reports), extracts relevant information (e.g., name, email, opening balance, closing balance), and sends the content to a Large Language Model (LLM) like OpenAI GPT to retrieve structured data. The app is fully containerized using Docker and provides a clean, dark-themed frontend for uploading PDFs.

## 🚀 Features

- Upload a PDF and extract key data using LLM
- RESTful API built using Spring Boot
- PDF text extraction using Apache PDFBox
- GPT-based language processing via OpenAI API
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
2. The backend extracts text using **Apache PDFBox**.
3. The text is sent to **OpenAI's GPT API** via OkHttp client.
4. The LLM responds with extracted fields like:
   - Name  
   - Email  
   - Opening Balance  
   - Closing Balance  
5. The backend returns a structured JSON response.



## 🌐 API Endpoint

### `POST /api/parse-pdf`

**Request:**

- Content-Type: `multipart/form-data`
- Parameter: `file` → PDF file

**Response:**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "openingBalance": "$5,000",
  "closingBalance": "$7,250"
}


```

---

### 🧪 **6. Getting Started (Local + Docker)**

```markdown
## 🛠️ Getting Started

### Prerequisites

- JDK 17+
- Maven
- OpenAI API Key
- Docker (optional)
```
### 🔧 Local Run

```bash
mvn clean install
mvn spring-boot:run
```

---

### 🐳 **7. Docker Instructions**

```markdown
### 🐳 Docker Setup

```bash
docker-compose up --build

```
---

### 🖥️ **8. Frontend Usage**

```markdown
## 💡 Frontend Preview

- Static HTML, CSS, JS in `src/main/resources/static`
- Accessible at: `http://localhost:64829/index.html`
- Upload a PDF and get results displayed in `<pre>` tag

```
## 🔐 Security Notes (For Production)

- Validate MIME type (`application/pdf`)
- Limit max file size in `application.properties`
- Add API authentication / rate-limiting
- Sanitize extracted text before LLM usage


## 🧠 LLM Prompt Sample

```text
Extract the following details from this PDF text:
- Full Name
- Email
- Opening Balance
- Closing Balance
Text:
[PDF Content Here...]

```







