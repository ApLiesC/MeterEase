package com.meterease.backend.service;

import com.meterease.backend.dto.ManagerResponse;
import com.meterease.backend.dto.RegisterRequest;
import com.meterease.backend.entity.Manager;
import com.meterease.backend.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ManagerResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmailAddress()
                .trim()
                .toLowerCase();

        if (!request.getPassword()
                .equals(request.getPasswordConfirmation())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords do not match"
            );
        }

        if (managerRepository.existsByEmailAddress(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email address is already registered"
            );
        }

        Manager manager = Manager.builder()
                .fullName(request.getFullName().trim())
                .emailAddress(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Manager savedManager = managerRepository.save(manager);

        return ManagerResponse.fromEntity(savedManager);
    }
}