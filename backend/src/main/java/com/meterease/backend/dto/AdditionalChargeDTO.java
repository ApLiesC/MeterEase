package com.meterease.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdditionalChargeDTO {

    private Integer additionalChargeId;

    private Integer roomId;

    @NotBlank
    private String chargeName;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal chargeAmount;
}