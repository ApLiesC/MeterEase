package com.meterease.backend.repository;

import com.meterease.backend.entity.BuildingSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingSettingsRepository
        extends JpaRepository<BuildingSettings, Integer> {

    Optional<BuildingSettings> findByBuildingBuildingId(Integer buildingId);

}