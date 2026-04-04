package com.finance.vaultflow.dto;

import lombok.*;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String role;
    private long expiresIn;
    private String id;
    private String name;
    private String email;
}