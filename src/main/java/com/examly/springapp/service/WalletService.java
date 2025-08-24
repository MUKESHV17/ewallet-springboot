package com.examly.springapp.service;

import com.examly.springapp.exception.BadRequestException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Transaction;
import com.examly.springapp.model.Wallet;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.WalletRepository;
import com.examly.springapp.repository.UserRepository;
import com.examly.springapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository; // Assuming you have this repository

    // --- THIS IS THE FIX: Add a check before creating a wallet ---
    public Wallet createWallet(String walletName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Enforce the rule in the code
        if (walletRepository.findByUser_UserId(user.getUserId()).isPresent()) {
            throw new BadRequestException("User already has a wallet.");
        }
        
        Wallet wallet = new Wallet();
        wallet.setWalletName(walletName);
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        
        return walletRepository.save(wallet);
    }
    // --- UPDATED: Renamed to topUp and made secure ---
    public Wallet topUp(Long walletId, BigDecimal amount, String username) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Top-up amount must be positive");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        
        // Security check: ensure the wallet belongs to the authenticated user
        if (!wallet.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Wallet does not belong to the current user");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        
        // Create a transaction record for the top-up
        Transaction transaction = new Transaction();
        transaction.setDestinationWallet(wallet); // Top-up only has a destination
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.TOP_UP); // Aligned with requirement
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
        
        return walletRepository.save(wallet);
    }

    // --- ADDED: Get all wallets for the logged-in user ---
    public List<Wallet> getWalletsForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return walletRepository.findByUser(user);
    }
    
     // --- REFACTORED: Transfer logic as per SRS ---
    @Transactional
    public Transaction transfer(String senderUsername, String recipientEmail, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be positive");
        }

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        
        User recipient = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient with email " + recipientEmail + " not found"));
        
        if (sender.getUserId().equals(recipient.getUserId())) {
            throw new BadRequestException("Cannot transfer funds to yourself.");
        }

        Wallet sourceWallet = walletRepository.findByUser_UserId(sender.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender does not have a wallet"));
        
        Wallet destinationWallet = walletRepository.findByUser_UserId(recipient.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient does not have a wallet"));

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        // Atomic operation: deduct from sender, add to recipient
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        destinationWallet.setBalance(destinationWallet.getBalance().add(amount));
        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        // Create a single transaction record for this transfer
        Transaction transaction = new Transaction();
        transaction.setSourceWallet(sourceWallet);
        transaction.setDestinationWallet(destinationWallet);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }
}