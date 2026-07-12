package com.meterease.backend.controller;

import com.meterease.backend.dto.BuildingDTO;
import com.meterease.backend.service.BuildingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(
            BuildingService buildingService
    ) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public List<BuildingDTO> getBuildings(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return buildingService.getBuildings(
                jwt.getSubject()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingDTO createBuilding(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BuildingDTO buildingDTO
    ) {
        return buildingService.createBuilding(
                jwt.getSubject(),
                buildingDTO
        );
    }

    @PutMapping("/{buildingId}")
    public BuildingDTO updateBuilding(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer buildingId,
            @Valid @RequestBody BuildingDTO buildingDTO
    ) {
        return buildingService.updateBuilding(
                jwt.getSubject(),
                buildingId,
                buildingDTO
        );
    }

    @DeleteMapping("/{buildingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBuilding(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer buildingId
    ) {
        buildingService.deleteBuilding(
                jwt.getSubject(),
                buildingId
        );
    }
}