package com.meterease.backend.service.impl;

import com.meterease.backend.dto.MeterReadingDTO;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.Manager;
import com.meterease.backend.entity.MeterReading;
import com.meterease.backend.entity.Room;
import com.meterease.backend.mapper.MeterReadingMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.ManagerRepository;
import com.meterease.backend.repository.MeterReadingRepository;
import com.meterease.backend.repository.RoomRepository;
import com.meterease.backend.service.MeterReadingService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MeterReadingServiceImpl
        implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final ManagerRepository managerRepository;

    public MeterReadingServiceImpl(
            MeterReadingRepository meterReadingRepository,
            RoomRepository roomRepository,
            BuildingRepository buildingRepository,
            ManagerRepository managerRepository
    ) {
        this.meterReadingRepository = meterReadingRepository;
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    @Transactional
    public void recordMeterReadings(
            String managerEmail,
            Integer buildingId,
            List<MeterReadingDTO> readings
    ) {
        Building building = getOwnedBuilding(
                managerEmail,
                buildingId
        );

        validateReadings(readings);

        Map<Integer, Room> rooms = getRooms(readings);

        validateAllRoomsFound(readings, rooms);
        validateRoomsBelongToBuilding(building, rooms);

        List<MeterReading> meterReadings =
                createMeterReadings(readings, rooms);

        meterReadingRepository.saveAll(meterReadings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeterReadingDTO> getMeterReadingHistory(
            String managerEmail,
            Integer roomId
    ) {
        Room room = getOwnedRoom(
                managerEmail,
                roomId
        );

        return meterReadingRepository
                .findByRoomRoomIdOrderByRecordedDateTimeDesc(
                        room.getRoomId()
                )
                .stream()
                .map(MeterReadingMapper::toDTO)
                .toList();
    }

    private Building getOwnedBuilding(
            String managerEmail,
            Integer buildingId
    ) {
        Manager manager = getManager(managerEmail);

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

    private Room getOwnedRoom(
            String managerEmail,
            Integer roomId
    ) {
        Manager manager = getManager(managerEmail);

        return roomRepository
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
    }

    private Manager getManager(String managerEmail) {
        return managerRepository
                .findByEmailAddress(managerEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Manager account not found"
                        )
                );
    }

    private Map<Integer, Room> getRooms(
            List<MeterReadingDTO> readings
    ) {
        List<Integer> roomIds = readings.stream()
                .map(MeterReadingDTO::getRoomId)
                .distinct()
                .toList();

        return roomRepository
                .findByRoomIdIn(roomIds)
                .stream()
                .collect(Collectors.toMap(
                        Room::getRoomId,
                        Function.identity()
                ));
    }

    private void validateReadings(
            List<MeterReadingDTO> readings
    ) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one meter reading is required"
            );
        }

        for (MeterReadingDTO reading : readings) {
            if (reading.getRoomId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Room ID is required"
                );
            }

            if (reading.getMeterReadingValue() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Meter reading value is required"
                );
            }

            if (reading.getMeterReadingValue() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Meter reading must be non-negative"
                );
            }

            if (reading.getUtilityType() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Utility type is required"
                );
            }
        }
    }

    private void validateAllRoomsFound(
            List<MeterReadingDTO> readings,
            Map<Integer, Room> rooms
    ) {
        for (MeterReadingDTO reading : readings) {
            if (!rooms.containsKey(reading.getRoomId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found: " + reading.getRoomId()
                );
            }
        }
    }

    private void validateRoomsBelongToBuilding(
            Building building,
            Map<Integer, Room> rooms
    ) {
        for (Room room : rooms.values()) {
            if (!room.getBuilding()
                    .getBuildingId()
                    .equals(building.getBuildingId())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Room does not belong to the selected building"
                );
            }
        }
    }

    private List<MeterReading> createMeterReadings(
            List<MeterReadingDTO> readings,
            Map<Integer, Room> rooms
    ) {
        return readings.stream()
                .map(dto -> MeterReading.builder()
                        .room(getRoom(dto, rooms))
                        .utilityType(dto.getUtilityType())
                        .meterReadingValue(
                                dto.getMeterReadingValue()
                        )
                        .meterImage(dto.getMeterImage())
                        .recordedDateTime(
                                LocalDateTime.now()
                        )
                        .build()
                )
                .toList();
    }

    private Room getRoom(
            MeterReadingDTO dto,
            Map<Integer, Room> rooms
    ) {
        Room room = rooms.get(dto.getRoomId());

        if (room == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Room not found"
            );
        }

        return room;
    }
}