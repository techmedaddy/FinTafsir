package com.fintafsir.service;

import com.fintafsir.model.PdfResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class PdfParserService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Replace with your actual OpenAI API key
    private static final String OPENAI_API_KEY = "your-openai-api-key";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public PdfResponse extractDataFromPdf(MultipartFile file) throws Exception {
        // 1. Read PDF content
        String pdfText;
        try (InputStream input = file.getInputStream(); PDDocument document = PDDocument.load(input)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfText = pdfStripper.getText(document);
        }

        // 2. Prepare prompt
        String prompt = "Extract name, email, opening balance, and closing balance from the following text:\n\n"
                + pdfText;

        // 3. Call OpenAI API
        String requestBody = objectMapper.writeValueAsString(new Object() {
            public final String model = "gpt-3.5-turbo";
            public final Object[] messages = new Object[] {
                    new Object() {
                        public final String role = "user";
                        public final String content = prompt;
                    }
            };
        });

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        // 4. Parse OpenAI response
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.at("/choices/0/message/content").asText();

        // 5. You can improve this by parsing structured JSON if you instruct LLM
        // accordingly
        return new PdfResponse(content.trim());
    }
}
