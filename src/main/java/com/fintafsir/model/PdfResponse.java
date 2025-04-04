package com.fintafsir.model;

public class PdfResponse {
    private String name;
    private String email;
    private String openingBalance;
    private String closingBalance;
    private String rawText; // Optional: to return raw LLM output if extraction fails

    // Constructors
    public PdfResponse() {
    }

    // Useful for raw text response
    public PdfResponse(String rawText) {
        this.rawText = rawText;
    }

    public PdfResponse(String name, String email, String openingBalance, String closingBalance) {
        this.name = name;
        this.email = email;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}
