package com.examly.springapp.controller;

import com.examly.springapp.dto.AuthResponse;
import com.examly.springapp.model.User;
import com.examly.springapp.service.UserService;
import com.examly.springapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://e-wallet-management-system.vercel.app")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // --- UPDATED: Register endpoint ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword()
            );
            
            String token = jwtUtil.generateToken(createdUser); // Pass the whole user object
            AuthResponse response = new AuthResponse(
                token, 
                "Registration successful", 
                createdUser.getUserId(), 
                createdUser.getUsername(),
                createdUser.getRole() // Add role to response
            );
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    // --- UPDATED: Login endpoint ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.getUsername());
            User loggedInUser = userService.login(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            
            String token = jwtUtil.generateToken(loggedInUser); // Pass the whole user object
            System.out.println("Generated token: " + token);
            
            AuthResponse response = new AuthResponse(
                token, 
                "Login successful", 
                loggedInUser.getUserId(), 
                loggedInUser.getUsername(),
                loggedInUser.getRole() // Add role to response
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }
    
    // --- ADDED: Endpoint for an Admin to create a new user ---
    @PostMapping("/admin/add")
    public ResponseEntity<User> addUserByAdmin(@RequestBody User user) {
        User createdUser = userService.addUserByAdmin(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Other debug endpoints remain the same
    @GetMapping("/test")
    public String test() { return "API is working!"; }

    @GetMapping("/debug/token")
    public ResponseEntity<String> debugToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No valid Authorization header found");
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok("Token received: " + token);
    }

    @GetMapping("/debug/auth")
    public ResponseEntity<String> debugAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok("Not authenticated");
        }
        return ResponseEntity.ok("Authenticated as: " + authentication.getName());
    }
}