package com.examly.springapp.service;

import com.examly.springapp.dto.TransactionResponseDTO;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Transaction;
import com.examly.springapp.model.User;
import com.examly.springapp.model.Wallet;
import com.examly.springapp.repository.TransactionRepository;
import com.examly.springapp.repository.UserRepository;
import com.examly.springapp.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors; // Add this import
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    // --- UPDATED to return a List of DTOs ---
    public List<TransactionResponseDTO> getTransactionsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Wallet wallet = walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user"));
        
        List<Transaction> transactions = transactionRepository.findBySourceWalletOrDestinationWalletOrderByTimestampDesc(wallet, wallet);

        return transactions.stream()
                           .map(this::convertToDto)
                           .collect(Collectors.toList());
    }
    
    // Admin method
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }
    
    // --- ADDED: Helper method to convert Entity to DTO ---
    private TransactionResponseDTO convertToDto(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setType(transaction.getType().name());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus().name());
        dto.setTimestamp(transaction.getTimestamp());

        if (transaction.getSourceWallet() != null && transaction.getSourceWallet().getUser() != null) {
            dto.setSenderEmail(transaction.getSourceWallet().getUser().getEmail());
        }

        if (transaction.getDestinationWallet() != null && transaction.getDestinationWallet().getUser() != null) {
            dto.setReceiverEmail(transaction.getDestinationWallet().getUser().getEmail());
        }
        
        return dto;
    }
}