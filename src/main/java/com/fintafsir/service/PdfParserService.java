package com.fintafsir.service;

import com.fintafsir.model.PdfResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class PdfParserService {

    private static final Logger log = LoggerFactory.getLogger(PdfParserService.class);

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiUrl;

    @Value("${openai.model}")
    private String openaiModel;

    @PostConstruct
    public void validateApiKey() {
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            log.warn("OPENAI_API_KEY is not set. Running in dummy mode (LLM calls disabled). ");
            return;
        }
        log.info("OpenAI API key configured (ends with ...{})",
                openaiApiKey.substring(Math.max(0, openaiApiKey.length() - 4)));
    }

    public PdfResponse extractDataFromPdf(MultipartFile file) throws Exception {
        // 1. Read PDF content
        String pdfText;
        try (InputStream input = file.getInputStream(); PDDocument document = PDDocument.load(input)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfText = pdfStripper.getText(document);
        }

        if (pdfText == null || pdfText.isBlank()) {
            throw new IllegalArgumentException("PDF contains no extractable text.");
        }

        // If API key is missing, return a dummy response for UI/testing
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            PdfResponse dummy = new PdfResponse();
            dummy.setName("Demo User");
            dummy.setEmail("demo@example.com");
            dummy.setOpeningBalance("$0.00");
            dummy.setClosingBalance("$0.00");
            dummy.setRawText("Dummy mode: OPENAI_API_KEY not set. Parsed text length=" + pdfText.length());
            return dummy;
        }

        // 2. Prepare prompt — instruct the LLM to respond with strict JSON only
        String prompt = "Extract the following fields from this bank statement / financial PDF text "
                + "and respond with ONLY a valid JSON object, no extra text:\n"
                + "{\n"
                + "  \"name\": \"<account holder name>\",\n"
                + "  \"email\": \"<email address or null>\",\n"
                + "  \"openingBalance\": \"<opening balance>\",\n"
                + "  \"closingBalance\": \"<closing balance>\"\n"
                + "}\n\n"
                + "If a field is not found, use null.\n\n"
                + "PDF Text:\n" + pdfText;

        // 3. Call OpenAI API
        String requestBody = objectMapper.writeValueAsString(new Object() {
            public final String model = openaiModel;
            public final Object[] messages = new Object[] {
                    new Object() {
                        public final String role = "system";
                        public final String content = "You are a data-extraction assistant. "
                                + "Always respond with valid JSON only, no markdown fences, no explanations.";
                    },
                    new Object() {
                        public final String role = "user";
                        public final String content = prompt;
                    }
            };
        });

        Request request = new Request.Builder()
                .url(openaiUrl)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "no body";
                throw new RuntimeException("OpenAI API returned HTTP " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();

            // 4. Parse OpenAI response
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.at("/choices/0/message/content").asText().trim();

            // 5. Try to parse structured JSON from LLM output
            return parseStructuredResponse(content);
        }
    }

    /**
     * Attempts to parse the LLM output as structured JSON into PdfResponse fields.
     * Falls back to returning the raw text if JSON parsing fails.
     */
    private PdfResponse parseStructuredResponse(String content) {
        try {
            // Strip markdown code fences if the LLM adds them despite instructions
            String cleaned = content;
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("\\n?```$", "");
            }

            JsonNode json = objectMapper.readTree(cleaned);

            PdfResponse pdfResponse = new PdfResponse();
            pdfResponse.setName(getTextOrNull(json, "name"));
            pdfResponse.setEmail(getTextOrNull(json, "email"));
            pdfResponse.setOpeningBalance(getTextOrNull(json, "openingBalance"));
            pdfResponse.setClosingBalance(getTextOrNull(json, "closingBalance"));
            pdfResponse.setRawText(content); // always include raw for transparency
            return pdfResponse;

        } catch (Exception e) {
            log.warn("Failed to parse structured JSON from LLM response, falling back to rawText: {}",
                    e.getMessage());
            return new PdfResponse(content);
        }
    }

    private String getTextOrNull(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }
}
