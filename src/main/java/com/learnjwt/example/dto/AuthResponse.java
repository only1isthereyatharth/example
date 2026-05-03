package com.learnjwt.example.dto;

import lombok.Data;

@Data
public class AuthResponse {
    public AuthResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }
    
    private String username;
    private String token;
}
