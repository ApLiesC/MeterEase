package com.meterease.backend.repository;

import com.meterease.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByBuildingBuildingId(Integer buildingId);

}