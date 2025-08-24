package com.examly.springapp.controller;

import com.examly.springapp.dto.TopUpRequest;
import com.examly.springapp.dto.TransferRequest;
import com.examly.springapp.dto.WalletRequest;
import com.examly.springapp.model.Transaction;
import com.examly.springapp.model.Wallet;
import com.examly.springapp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "http://localhost:8081")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // --- ADDED: Get all wallets for the authenticated user ---
    @GetMapping
    public ResponseEntity<List<Wallet>> getUserWallets(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(walletService.getWalletsForUser(username));
    }

    // --- UPDATED: Secure wallet creation ---
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequest walletRequest, Authentication authentication) {
        String username = authentication.getName();
        Wallet createdWallet = walletService.createWallet(walletRequest.getWalletName(), username);
        return ResponseEntity.ok(createdWallet);
    }

    // --- UPDATED: Secure top-up endpoint ---
    @PostMapping("/{walletId}/top-up")
    public ResponseEntity<Wallet> topUpWallet(
            @PathVariable Long walletId,
            @RequestBody TopUpRequest topUpRequest,
            Authentication authentication) {
        String username = authentication.getName();
        Wallet updatedWallet = walletService.topUp(walletId, topUpRequest.getAmount(), username);
        return ResponseEntity.ok(updatedWallet);
    }

     // --- ADDED: Secure fund transfer endpoint ---
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(
            @RequestBody TransferRequest transferRequest,
            Authentication authentication) {
        String senderUsername = authentication.getName();
        Transaction transaction = walletService.transfer(
                senderUsername,
                transferRequest.getRecipientEmail(),
                transferRequest.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }
}