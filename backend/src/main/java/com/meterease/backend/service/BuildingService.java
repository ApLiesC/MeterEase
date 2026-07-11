package com.meterease.backend.service;

import com.meterease.backend.dto.BuildingDTO;

import java.util.List;

public interface BuildingService {

    BuildingDTO createBuilding(BuildingDTO buildingDTO);

    List<BuildingDTO> getBuildings();

    BuildingDTO updateBuilding(Integer buildingId, BuildingDTO buildingDTO);
    
    void deleteBuilding(Integer buildingId);
}