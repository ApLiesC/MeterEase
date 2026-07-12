package com.meterease.backend.service;

import com.meterease.backend.dto.RoomDTO;

import java.util.List;

public interface RoomService {

    RoomDTO createRoom(RoomDTO roomDTO);

    List<RoomDTO> getRooms(Integer buildingId);

    RoomDTO updateRoom(Integer roomId, RoomDTO roomDTO);

    void deleteRoom(Integer roomId);

}