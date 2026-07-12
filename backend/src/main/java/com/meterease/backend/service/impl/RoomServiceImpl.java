package com.meterease.backend.service.impl;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.dto.GenerateRoomsRequest;
import com.meterease.backend.dto.RoomDTO;
import com.meterease.backend.entity.AdditionalCharge;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.Manager;
import com.meterease.backend.entity.Room;
import com.meterease.backend.enums.RoomNamePattern;
import com.meterease.backend.mapper.RoomMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.ManagerRepository;
import com.meterease.backend.repository.RoomRepository;
import com.meterease.backend.service.RoomService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final ManagerRepository managerRepository;

    @Override
    @Transactional
    public RoomDTO createRoom(
            String managerEmail,
            RoomDTO roomDTO
    ) {
        Integer buildingId = roomDTO.getBuildingId();

        Building building = getOwnedBuilding(
                managerEmail,
                buildingId
        );

        String roomName = roomDTO.getRoomName().trim();

        validateRoomNameIsUnique(
                buildingId,
                roomName
        );

        Room room = new Room();

        room.setBuilding(building);
        room.setTenantId(roomDTO.getTenantId());
        room.setRoomName(roomName);
        room.setRentAmount(roomDTO.getRentAmount());

        addAdditionalCharges(
                room,
                roomDTO.getAdditionalCharges()
        );

        Room savedRoom = roomRepository.save(room);

        return RoomMapper.toDTO(savedRoom);
    }

    @Override
    @Transactional
    public List<RoomDTO> generateRooms(
            String managerEmail,
            GenerateRoomsRequest request
    ) {
        Integer buildingId = request.getBuildingId();

        Building building = getOwnedBuilding(
                managerEmail,
                buildingId
        );

        validateGenerationRequest(request);

        List<String> generatedRoomNames =
                generateRoomNames(request);

        validateGeneratedRoomNames(
                buildingId,
                generatedRoomNames
        );

        List<Room> rooms = new ArrayList<>();

        for (String roomName : generatedRoomNames) {
            Room room = new Room();

            room.setBuilding(building);
            room.setTenantId(null);
            room.setRoomName(roomName);
            room.setRentAmount(request.getRentAmount());

            addAdditionalCharges(
                    room,
                    request.getAdditionalCharges()
            );

            rooms.add(room);
        }

        List<Room> savedRooms =
                roomRepository.saveAll(rooms);

        return savedRooms.stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRooms(
            String managerEmail,
            Integer buildingId
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        List<Room> rooms;

        if (buildingId == null) {
            rooms = roomRepository
                    .findAllByBuildingManagerManagerId(
                            manager.getManagerId()
                    );
        } else {
            getOwnedBuilding(
                    managerEmail,
                    buildingId
            );

            rooms = roomRepository
                    .findAllByBuildingBuildingIdAndBuildingManagerManagerId(
                            buildingId,
                            manager.getManagerId()
                    );
        }

        return rooms.stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public RoomDTO updateRoom(
            String managerEmail,
            Integer roomId,
            RoomDTO roomDTO
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        Room room = roomRepository
                .findByRoomIdAndBuildingManagerManagerId(
                        roomId,
                        manager.getManagerId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        )
                );

        String updatedRoomName =
                roomDTO.getRoomName().trim();

        boolean roomNameChanged =
                !room.getRoomName()
                        .equalsIgnoreCase(updatedRoomName);

        if (roomNameChanged) {
            validateRoomNameIsUnique(
                    room.getBuilding().getBuildingId(),
                    updatedRoomName
            );
        }

        if (roomDTO.getBuildingId() != null
                && !roomDTO.getBuildingId().equals(
                        room.getBuilding().getBuildingId()
                )) {

            Building newBuilding = getOwnedBuilding(
                    managerEmail,
                    roomDTO.getBuildingId()
            );

            validateRoomNameIsUnique(
                    newBuilding.getBuildingId(),
                    updatedRoomName
            );

            room.setBuilding(newBuilding);
        }

        room.setRoomName(updatedRoomName);
        room.setRentAmount(roomDTO.getRentAmount());
        room.setTenantId(roomDTO.getTenantId());

        replaceAdditionalCharges(
                room,
                roomDTO.getAdditionalCharges()
        );

        Room updatedRoom = roomRepository.save(room);

        return RoomMapper.toDTO(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(
            String managerEmail,
            Integer roomId
    ) {
        Manager manager = getManagerByEmail(managerEmail);

        Room room = roomRepository
                .findByRoomIdAndBuildingManagerManagerId(
                        roomId,
                        manager.getManagerId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        )
                );

        roomRepository.delete(room);
    }

    private void validateGenerationRequest(
            GenerateRoomsRequest request
    ) {
        if (request.getNumberOfRooms() == null
                || request.getNumberOfRooms() < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one room must be created"
            );
        }

        if (request.getStartingNumber() == null
                || request.getStartingNumber() < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Starting number must be at least 1"
            );
        }

        if (request.getRoomNamePattern() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room naming pattern is required"
            );
        }

        if (request.getRoomNamePattern()
                == RoomNamePattern.PREFIX_AND_NUMBER) {

            if (request.getPrefix() == null
                    || request.getPrefix().isBlank()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Prefix is required when using prefix and number"
                );
            }
        }
    }

    private List<String> generateRoomNames(
            GenerateRoomsRequest request
    ) {
        List<String> roomNames = new ArrayList<>();

        for (
                int index = 0;
                index < request.getNumberOfRooms();
                index++
        ) {
            int currentNumber =
                    request.getStartingNumber() + index;

            String roomName;

            if (request.getRoomNamePattern()
                    == RoomNamePattern.NUMBER_ONLY) {

                roomName = String.valueOf(currentNumber);

            } else {
                roomName =
                        request.getPrefix().trim()
                                + currentNumber;
            }

            roomNames.add(roomName);
        }

        return roomNames;
    }

    private void validateGeneratedRoomNames(
            Integer buildingId,
            List<String> roomNames
    ) {
        for (String roomName : roomNames) {
            validateRoomNameIsUnique(
                    buildingId,
                    roomName
            );
        }
    }

    private void validateRoomNameIsUnique(
            Integer buildingId,
            String roomName
    ) {
        boolean roomExists = roomRepository
                .existsByBuildingBuildingIdAndRoomNameIgnoreCase(
                        buildingId,
                        roomName.trim()
                );

        if (roomExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room already exists: " + roomName
            );
        }
    }

    private void addAdditionalCharges(
            Room room,
            List<AdditionalChargeDTO> chargeDTOs
    ) {
        if (chargeDTOs == null) {
            return;
        }

        for (AdditionalChargeDTO chargeDTO : chargeDTOs) {
            AdditionalCharge charge =
                    new AdditionalCharge();

            charge.setChargeName(
                    chargeDTO.getChargeName().trim()
            );

            charge.setChargeAmount(
                    chargeDTO.getChargeAmount()
            );

            charge.setRoom(room);

            room.getAdditionalCharges().add(charge);
        }
    }

    private void replaceAdditionalCharges(
            Room room,
            List<AdditionalChargeDTO> chargeDTOs
    ) {
        room.getAdditionalCharges().clear();

        addAdditionalCharges(
                room,
                chargeDTOs
        );
    }

    private Building getOwnedBuilding(
            String managerEmail,
            Integer buildingId
    ) {
        if (buildingId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Building is required"
            );
        }

        Manager manager = getManagerByEmail(managerEmail);

        return buildingRepository
                .findByBuildingIdAndManagerManagerId(
                        buildingId,
                        manager.getManagerId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Building not found"
                        )
                );
    }

    private Manager getManagerByEmail(
            String managerEmail
    ) {
        return managerRepository
                .findByEmailAddress(managerEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Manager account not found"
                        )
                );
    }
}