package com.fintafsir.controller;

import com.fintafsir.model.PdfResponse;
import com.fintafsir.service.PdfParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PdfController {

    /** Maximum allowed file size: 10 MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final PdfParserService pdfParserService;

    @Autowired
    public PdfController(PdfParserService pdfParserService) {
        this.pdfParserService = pdfParserService;
    }

    @PostMapping("/parse-pdf")
    public ResponseEntity<?> parsePdf(@RequestParam("file") MultipartFile file) {

        // --- Validation ---

        // 1. Reject empty / missing file
        if (file.isEmpty()) {
            return badRequest("No file uploaded or file is empty.");
        }

        // 2. Reject non-PDF content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            return badRequest("Only PDF files are accepted. Received content type: " + contentType);
        }

        // 3. Reject files whose original name doesn't end with .pdf
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            return badRequest("File must have a .pdf extension.");
        }

        // 4. Reject oversized files
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(errorBody("File exceeds the maximum allowed size of 10 MB."));
        }

        // --- Processing ---
        try {
            PdfResponse response = pdfParserService.extractDataFromPdf(file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("Error parsing PDF: " + e.getMessage()));
        }
    }

    // --- Helpers ---

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(message));
    }

    private Map<String, String> errorBody(String message) {
        return Map.of("error", message);
    }
}
