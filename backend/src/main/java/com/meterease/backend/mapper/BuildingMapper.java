package com.meterease.backend.mapper;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.entity.Building;

public class BuildingMapper {

    public static BuildingDTO toDTO(Building building) {
        BuildingDTO dto = new BuildingDTO();

        dto.setBuildingId(building.getBuildingId());
        dto.setManagerId(building.getManagerId());
        dto.setBuildingName(building.getBuildingName());
        dto.setAddress(building.getAddress());

        return dto;
    }


    public static Building toEntity(BuildingDTO dto) {
        Building building = new Building();

        building.setBuildingId(dto.getBuildingId());
        building.setManagerId(dto.getManagerId());
        building.setBuildingName(dto.getBuildingName());
        building.setAddress(dto.getAddress());

        return building;
    }
}