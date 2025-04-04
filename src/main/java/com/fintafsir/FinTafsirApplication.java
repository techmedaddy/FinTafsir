package com.fintafsir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinTafsirApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinTafsirApplication.class, args);
        System.out.println("🚀 FinTafsir is running on http://localhost:64829");
    }
}
