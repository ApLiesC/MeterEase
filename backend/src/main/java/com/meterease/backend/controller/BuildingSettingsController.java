package com.meterease.backend.controller;

import com.meterease.backend.dto.BuildingSettingsDTO;
import com.meterease.backend.service.BuildingSettingsService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buildings/{buildingId}/settings")
public class BuildingSettingsController {

    private final BuildingSettingsService buildingSettingsService;


    public BuildingSettingsController(
            BuildingSettingsService buildingSettingsService
    ) {
        this.buildingSettingsService = buildingSettingsService;
    }


    @PostMapping
    public BuildingSettingsDTO createBuildingSettings(
            @PathVariable Integer buildingId,
            @RequestBody @Valid BuildingSettingsDTO dto
    ) {

        return buildingSettingsService
                .createBuildingSettings(buildingId, dto);
    }


    @GetMapping
    public BuildingSettingsDTO getBuildingSettings(
            @PathVariable Integer buildingId
    ) {

        return buildingSettingsService
                .getBuildingSettings(buildingId);
    }


    @PutMapping
    public BuildingSettingsDTO updateBuildingSettings(
            @PathVariable Integer buildingId,
            @RequestBody @Valid BuildingSettingsDTO dto
    ) {

        return buildingSettingsService
                .updateBuildingSettings(buildingId, dto);
    }
}