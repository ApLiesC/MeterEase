package com.meterease.backend.repository;

import com.meterease.backend.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Integer> {

}