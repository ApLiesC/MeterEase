package com.meterease.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email address")
    private String emailAddress;

    @NotBlank(message = "Password is required")
    private String password;

}