package com.examly.springapp.service;

import com.examly.springapp.exception.BadRequestException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.User;
import com.examly.springapp.model.Role; // --- ADDED ---
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ old method (kept for your test cases)
    public User createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.USER); // Also update here for consistency
        return userRepository.save(user);
    }

    // ✅ new method (for actual registration with password) - UPDATED
    public User createUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already in use");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER); // Assign default role
        return userRepository.save(user);
    }
    
    // --- ADDED: Method for Admins to add new users ---
    public User addUserByAdmin(User newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        // Ensure role is not null, default to USER if not provided
        if (newUser.getRole() == null) {
            newUser.setRole(Role.USER);
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // --- UPDATED: To allow role updates ---
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(userDetails.getUsername()) &&
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (!user.getEmail().equals(userDetails.getEmail()) &&
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole()); // Allow role update
        
        // Only update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // ✅ login method with password hashing check
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Check if stored password is already hashed (starts with BCrypt pattern)
        if (user.getPassword().startsWith("$2a$")) {
            // Password is already hashed, use matches()
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadRequestException("Invalid password");
            }
        } else {
            // Password is plain text, compare directly (for legacy users)
            if (!password.equals(user.getPassword())) {
                throw new BadRequestException("Invalid password");
            }
            // Auto-migrate to hashed password
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            System.out.println("Auto-migrated password to hashed version for user: " + username);
        }
    
        return user;
    }
}