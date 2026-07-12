package com.meterease.backend.service.impl;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.Manager;
import com.meterease.backend.mapper.BuildingMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.ManagerRepository;
import com.meterease.backend.service.BuildingService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final ManagerRepository managerRepository;

    @Override
    @Transactional
    public BuildingDTO createBuilding(
            String managerEmail,
            BuildingDTO buildingDTO
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        Building building = BuildingMapper.toEntity(buildingDTO);

        building.setBuildingName(
                buildingDTO.getBuildingName().trim()
        );

        building.setAddress(
                buildingDTO.getAddress().trim()
        );

        building.setManager(manager);

        Building savedBuilding =
                buildingRepository.save(building);

        return BuildingMapper.toDTO(savedBuilding);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingDTO> getBuildings(
            String managerEmail
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        return buildingRepository
                .findAllByManagerManagerId(
                        manager.getManagerId()
                )
                .stream()
                .map(BuildingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BuildingDTO updateBuilding(
            String managerEmail,
            Integer buildingId,
            BuildingDTO buildingDTO
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        Building existingBuilding = buildingRepository
                .findByBuildingIdAndManagerManagerId(
                        buildingId,
                        manager.getManagerId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Building not found"
                        )
                );

        existingBuilding.setBuildingName(
                buildingDTO.getBuildingName().trim()
        );

        existingBuilding.setAddress(
                buildingDTO.getAddress().trim()
        );

        Building updatedBuilding =
                buildingRepository.save(existingBuilding);

        return BuildingMapper.toDTO(updatedBuilding);
    }

    @Override
    @Transactional
    public void deleteBuilding(
            String managerEmail,
            Integer buildingId
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        Building building = buildingRepository
                .findByBuildingIdAndManagerManagerId(
                        buildingId,
                        manager.getManagerId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Building not found"
                        )
                );

        buildingRepository.delete(building);
    }

    private Manager getManagerByEmail(String managerEmail) {
        return managerRepository
                .findByEmailAddress(managerEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Manager account not found"
                        )
                );
    }
}