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
import com.meterease.backend.dto.ManagerResponse;
import com.meterease.backend.entity.Manager;
import com.meterease.backend.repository.ManagerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ManagerRepository managerRepository;
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

@GetMapping("/me")
public ManagerResponse getCurrentManager(
        @AuthenticationPrincipal Jwt jwt
) {
    String emailAddress = jwt.getSubject();

    Manager manager = managerRepository
            .findByEmailAddress(emailAddress)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Manager account not found"
            ));

    return ManagerResponse.fromEntity(manager);
}
}