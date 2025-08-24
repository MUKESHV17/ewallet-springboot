package com.examly.springapp.controller;

import com.examly.springapp.dto.TransactionResponseDTO;
import com.examly.springapp.model.Transaction;
import com.examly.springapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:8081")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

   // --- UPDATED to return the new DTO ---
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionsForUser(username));
    }

    // --- ADDED: Endpoint for admin to get all transactions ---
    @GetMapping("/admin/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}