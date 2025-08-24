package com.examly.springapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Good practice to specify table name
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Store role as a string ("USER", "ADMIN")
    @Column(nullable = false)
    private Role role; // --- ADDED ---

    public User() {}

    // --- Getters & Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // --- ADDED getter and setter for role ---
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}