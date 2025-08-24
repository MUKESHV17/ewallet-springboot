package com.examly.springapp.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String recipientEmail;
    private BigDecimal amount;

    // Getters and Setters
    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}