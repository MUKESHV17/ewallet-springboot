package com.examly.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EWalletManagementSystemApplication {
    public static void main(String[] args) {
        // Set the property before starting the application
        System.setProperty("spring.transaction.default-timeout", "30s");
        SpringApplication.run(EWalletManagementSystemApplication.class, args);
    }
}