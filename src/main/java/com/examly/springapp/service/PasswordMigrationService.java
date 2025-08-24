package com.examly.springapp.service;

import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordMigrationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void migratePasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Check if password is not hashed (doesn't start with BCrypt pattern)
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                System.out.println("Migrating password for user: " + user.getUsername());
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                System.out.println("Password migrated successfully for: " + user.getUsername());
            }
        }
    }
}