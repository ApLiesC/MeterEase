package com.meterease.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeminiValidationResponse {

    private Integer reading;

    private Double confidence;

    private String reason;

}