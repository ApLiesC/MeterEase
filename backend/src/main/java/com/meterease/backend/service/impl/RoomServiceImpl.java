package com.meterease.backend.service.impl;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.dto.RoomDTO;
import com.meterease.backend.entity.AdditionalCharge;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.Room;
import com.meterease.backend.mapper.RoomMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.RoomRepository;
import com.meterease.backend.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;


    public RoomServiceImpl(
            RoomRepository roomRepository,
            BuildingRepository buildingRepository
    ) {
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) {

        Building building = buildingRepository.findById(roomDTO.getBuildingId())
                .orElseThrow(() ->
                        new RuntimeException("Building not found")
                );


        Room room = new Room();

        room.setBuilding(building);
        room.setTenantId(roomDTO.getTenantId());
        room.setRoomName(roomDTO.getRoomName());
        room.setRentAmount(roomDTO.getRentAmount());

        // Add additional charges
        if (roomDTO.getAdditionalCharges() != null) {

            for (AdditionalChargeDTO chargeDTO :
                    roomDTO.getAdditionalCharges()) {

                AdditionalCharge charge =
                        new AdditionalCharge();

                charge.setChargeName(
                        chargeDTO.getChargeName()
                );

                charge.setChargeAmount(
                        chargeDTO.getChargeAmount()
                );

                // establish relationship
                charge.setRoom(room);

                room.getAdditionalCharges()
                        .add(charge);
            }
        }

        Room savedRoom = roomRepository.save(room);

        return RoomMapper.toDTO(savedRoom);
    }

    @Override
    @Transactional
    public List<RoomDTO> getRooms(Integer buildingId) {

        List<Room> rooms;

        if (buildingId == null) {
            rooms = roomRepository.findAll();

        } else {

            rooms = roomRepository
                    .findByBuildingBuildingId(buildingId);
        }

        return rooms.stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    @Override
    public RoomDTO updateRoom(
            Integer roomId,
            RoomDTO roomDTO
    ) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );

        Building building =
                buildingRepository.findById(roomDTO.getBuildingId())
                .orElseThrow(() ->
                        new RuntimeException("Building not found")
                );

        room.setBuilding(building);
        room.setTenantId(roomDTO.getTenantId());
        room.setRoomName(roomDTO.getRoomName());
        room.setRentAmount(roomDTO.getRentAmount());

        Room updatedRoom =
                roomRepository.save(room);

        return RoomMapper.toDTO(updatedRoom);
    }

    @Override
    public void deleteRoom(Integer roomId) {

        if (!roomRepository.existsById(roomId)) {

            throw new RuntimeException("Room not found");

        }

        roomRepository.deleteById(roomId);
    }
}