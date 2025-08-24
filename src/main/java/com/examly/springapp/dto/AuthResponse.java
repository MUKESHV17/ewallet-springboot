package com.examly.springapp.dto;

import com.examly.springapp.model.Role;

public class AuthResponse {
    private String token;
    private String message;
    private Long userId;
    private String username;
    private Role role; // Field for the role

    // Default constructor
    public AuthResponse() {}

    // Constructor with all fields (this is the one you need)
    public AuthResponse(String token, String message, Long userId, String username, Role role) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters for all fields

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}


