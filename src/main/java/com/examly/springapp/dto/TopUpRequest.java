package com.examly.springapp.dto;

import java.math.BigDecimal;

public class TopUpRequest {
    private BigDecimal amount;

    // Getter and Setter
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}