package com.fintafsir.controller;

import com.fintafsir.model.PdfResponse;
import com.fintafsir.service.PdfParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class PdfController {

    private final PdfParserService pdfParserService;

    @Autowired
    public PdfController(PdfParserService pdfParserService) {
        this.pdfParserService = pdfParserService;
    }

    @PostMapping("/parse-pdf")
    public ResponseEntity<?> parsePdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No file uploaded");
        }

        try {
            PdfResponse response = pdfParserService.extractDataFromPdf(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing PDF: " + e.getMessage());
        }
    }
}
