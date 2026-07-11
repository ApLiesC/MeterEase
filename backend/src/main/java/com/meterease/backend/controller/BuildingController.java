package com.meterease.backend.controller;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public List<BuildingDTO> getBuildings() {
        return buildingService.getBuildings();
    }

    @PostMapping
    public BuildingDTO createBuilding(@Valid @RequestBody BuildingDTO buildingDTO) {
        return buildingService.createBuilding(buildingDTO);
    }

    @PutMapping("/{buildingId}")
    public BuildingDTO updateBuilding(
            @PathVariable Integer buildingId,
            @Valid @RequestBody BuildingDTO buildingDTO) {

        return buildingService.updateBuilding(buildingId, buildingDTO);
    }

    @DeleteMapping("/{buildingId}")
    public void deleteBuilding(@PathVariable Integer buildingId) {
        buildingService.deleteBuilding(buildingId);
    }
}