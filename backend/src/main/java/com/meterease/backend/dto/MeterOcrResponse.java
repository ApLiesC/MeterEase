package com.meterease.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MeterOcrResponse {

    private String extractedText;

    private Integer suggestedReading;

    private List<Integer> detectedNumbers;
}