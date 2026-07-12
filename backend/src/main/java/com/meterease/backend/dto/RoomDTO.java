package com.meterease.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomDTO {

    private Integer roomId;

    @NotNull
    private Integer buildingId;

    private Integer tenantId;

    @NotBlank
    private String roomName;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal rentAmount;
}