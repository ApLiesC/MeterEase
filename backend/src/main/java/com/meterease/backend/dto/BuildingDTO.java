package com.meterease.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingDTO {

    private Integer buildingId;

    // Returned by the backend; not required in create/update requests.
    private Integer managerId;

    @NotBlank(message = "Building name is required")
    private String buildingName;

    @NotBlank(message = "Address is required")
    private String address;
}