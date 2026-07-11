package com.meterease.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingDTO {

    private Integer buildingId;
    private Integer managerId; //remove when we have authentication

    @NotBlank
    private String buildingName;

    @NotBlank
    private String address;

}