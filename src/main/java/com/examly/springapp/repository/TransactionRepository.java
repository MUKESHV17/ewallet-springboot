package com.examly.springapp.repository;

import com.examly.springapp.model.Transaction;
import com.examly.springapp.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findBySourceWalletOrDestinationWalletOrderByTimestampDesc(Wallet source, Wallet destination);

    // --- ADDED: For Admin to fetch all transactions ---
    List<Transaction> findAllByOrderByTimestampDesc();
}