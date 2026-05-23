package com.techstoresecureapp.dto;

public class AuthResponse {

    private String message;
    private String email;
    private String role;
    private String token;

    public AuthResponse(String message, String email, String role, String token) {
        this.message = message;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
