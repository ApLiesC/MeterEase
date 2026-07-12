package com.meterease.backend.service;

import com.meterease.backend.dto.BuildingDTO;

import java.util.List;

public interface BuildingService {

    BuildingDTO createBuilding(
            String managerEmail,
            BuildingDTO buildingDTO
    );

    List<BuildingDTO> getBuildings(String managerEmail);

    BuildingDTO updateBuilding(
            String managerEmail,
            Integer buildingId,
            BuildingDTO buildingDTO
    );

    void deleteBuilding(
            String managerEmail,
            Integer buildingId
    );
}