package com.meterease.backend.service.impl;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.entity.Building;
import com.meterease.backend.mapper.BuildingMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.service.BuildingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public BuildingDTO createBuilding(BuildingDTO buildingDTO) {
        Building building = BuildingMapper.toEntity(buildingDTO);
        Building savedBuilding = buildingRepository.save(building);
        return BuildingMapper.toDTO(savedBuilding);
    }


    @Override
    public List<BuildingDTO> getBuildings() {

        return buildingRepository.findAll()
            .stream()
            .map(BuildingMapper::toDTO)
            .toList();
    }

    @Override
    public BuildingDTO updateBuilding(Integer buildingId, BuildingDTO buildingDTO) {

        Building existingBuilding = buildingRepository.findById(buildingId)
            .orElseThrow(() -> 
                new RuntimeException("Building not found")
            );

        existingBuilding.setBuildingName(buildingDTO.getBuildingName());
        existingBuilding.setAddress(buildingDTO.getAddress());

        Building updatedBuilding = buildingRepository.save(existingBuilding);

        return BuildingMapper.toDTO(updatedBuilding);
    }


    @Override
    public void deleteBuilding(Integer buildingId) {

        if (!buildingRepository.existsById(buildingId)) {
            throw new RuntimeException("Building not found");
        }

        buildingRepository.deleteById(buildingId);
    }
}