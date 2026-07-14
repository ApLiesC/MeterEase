package com.meterease.backend.repository;

import com.meterease.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository
        extends JpaRepository<Room, Integer> {

    List<Room> findByRoomIdIn(
            List<Integer> roomIds
    );

    List<Room> findByBuildingBuildingId(
            Integer buildingId
    );

    List<Room> findAllByBuildingManagerManagerId(
            Integer managerId
    );

    List<Room>
    findAllByBuildingBuildingIdAndBuildingManagerManagerId(
            Integer buildingId,
            Integer managerId
    );

    Optional<Room>
    findByRoomIdAndBuildingManagerManagerId(
            Integer roomId,
            Integer managerId
    );

    boolean
    existsByBuildingBuildingIdAndRoomNameIgnoreCase(
            Integer buildingId,
            String roomName
    );
}