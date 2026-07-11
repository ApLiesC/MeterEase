package com.meterease.backend.controller;

import com.meterease.backend.dto.ManagerResponse;
import com.meterease.backend.dto.RegisterRequest;
import com.meterease.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.meterease.backend.dto.AuthResponse;
import com.meterease.backend.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ManagerResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
public AuthResponse login(
        @Valid @RequestBody LoginRequest request
) {
    return authService.login(request);
}
}