package com.meterease.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private Long managerId;
    private String fullName;
    private String emailAddress;
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}