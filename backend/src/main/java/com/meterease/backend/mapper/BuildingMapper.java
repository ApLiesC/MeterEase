package com.meterease.backend.mapper;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.entity.Building;

public final class BuildingMapper {

    private BuildingMapper() {
    }

    public static BuildingDTO toDTO(Building building) {
        BuildingDTO dto = new BuildingDTO();

        dto.setBuildingId(building.getBuildingId());

        if (building.getManager() != null) {
            dto.setManagerId(
                    building.getManager().getManagerId()
            );
        }

        dto.setBuildingName(building.getBuildingName());
        dto.setAddress(building.getAddress());

        return dto;
    }

    public static Building toEntity(BuildingDTO dto) {
        Building building = new Building();

        building.setBuildingName(dto.getBuildingName());
        building.setAddress(dto.getAddress());

        // Do not set manager here.
        // BuildingService assigns the authenticated manager.

        return building;
    }
}