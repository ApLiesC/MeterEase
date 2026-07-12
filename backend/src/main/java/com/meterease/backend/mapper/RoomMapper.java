package com.meterease.backend.mapper;

import com.meterease.backend.dto.RoomDTO;
import com.meterease.backend.entity.Room;

public class RoomMapper {

    public static RoomDTO toDTO(Room room) {

        RoomDTO dto = new RoomDTO();

        dto.setRoomId(room.getRoomId());
        dto.setBuildingId(room.getBuilding().getBuildingId());
        dto.setTenantId(room.getTenantId());
        dto.setRoomName(room.getRoomName());
        dto.setRentAmount(room.getRentAmount());
        dto.setAdditionalCharges(
            room.getAdditionalCharges()
                .stream()
                .map(AdditionalChargeMapper::toDTO)
                .toList()
        );

        return dto;
    }

}