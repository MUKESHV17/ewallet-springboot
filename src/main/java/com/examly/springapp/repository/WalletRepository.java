package com.examly.springapp.repository;

import com.examly.springapp.model.User;
import com.examly.springapp.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    List<Wallet> findByUser(User user);

    // --- UPDATED: More specific method for one-wallet-per-user model ---
    Optional<Wallet> findByUser_UserId(Long userId);
}