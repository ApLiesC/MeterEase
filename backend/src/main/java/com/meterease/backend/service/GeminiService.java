package com.meterease.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GeminiService {

    String validateReading(
            MultipartFile image,
            String ocrText,
            List<Integer> detectedNumbers
    );
}