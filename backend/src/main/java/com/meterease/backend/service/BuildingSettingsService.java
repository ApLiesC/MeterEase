package com.meterease.backend.service;

import com.meterease.backend.dto.BuildingSettingsDTO;

public interface BuildingSettingsService {

    BuildingSettingsDTO createBuildingSettings(
            Integer buildingId,
            BuildingSettingsDTO dto
    );

    BuildingSettingsDTO getBuildingSettings(
            Integer buildingId
    );

    BuildingSettingsDTO updateBuildingSettings(
            Integer buildingId,
            BuildingSettingsDTO dto
    );
}