package com.project.back_end.DTO;

public class Login {
    
    // 1. 'email' field
    private String email;
    
    // 2. 'password' field
    private String password;

    // 3. Constructors
    // Explicit default constructor (No-args constructor required for JSON serialization libraries like Jackson)
    public Login() {
    }

    // Optional but highly recommended: Parameterized constructor for easy initialization in tests/services
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 4. Getters and Setters
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
}