package com.meterease.backend.repository;

import com.meterease.backend.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository
        extends JpaRepository<Building, Integer> {

    List<Building> findAllByManagerManagerId(Integer managerId);

    Optional<Building> findByBuildingIdAndManagerManagerId(
            Integer buildingId,
            Integer managerId
    );
}