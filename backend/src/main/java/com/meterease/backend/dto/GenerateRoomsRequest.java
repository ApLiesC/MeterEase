package com.meterease.backend.dto;

import com.meterease.backend.enums.RoomNamePattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GenerateRoomsRequest {

    @NotNull(message = "Building is required")
    private Integer buildingId;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "At least one room must be created")
    private Integer numberOfRooms;

    @NotNull(message = "Room naming pattern is required")
    private RoomNamePattern roomNamePattern;

    private String prefix;

    @NotNull(message = "Starting number is required")
    @Min(value = 1, message = "Starting number must be at least 1")
    private Integer startingNumber;

    @NotNull(message = "Rent amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Rent amount cannot be negative"
    )
    private BigDecimal rentAmount;

    @Valid
    private List<AdditionalChargeDTO> additionalCharges =
            new ArrayList<>();
}