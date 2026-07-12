package com.meterease.backend.service;

import com.meterease.backend.dto.GenerateRoomsRequest;
import com.meterease.backend.dto.RoomDTO;

import java.util.List;

public interface RoomService {

    RoomDTO createRoom(
            String managerEmail,
            RoomDTO roomDTO
    );

    List<RoomDTO> generateRooms(
            String managerEmail,
            GenerateRoomsRequest request
    );

    List<RoomDTO> getRooms(
            String managerEmail,
            Integer buildingId
    );

    RoomDTO updateRoom(
            String managerEmail,
            Integer roomId,
            RoomDTO roomDTO
    );

    void deleteRoom(
            String managerEmail,
            Integer roomId
    );
}