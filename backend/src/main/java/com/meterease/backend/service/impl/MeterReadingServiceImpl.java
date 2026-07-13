package com.meterease.backend.service.impl;

import com.meterease.backend.dto.MeterReadingDTO;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.MeterReading;
import com.meterease.backend.entity.Room;
import com.meterease.backend.mapper.MeterReadingMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.MeterReadingRepository;
import com.meterease.backend.repository.RoomRepository;
import com.meterease.backend.service.MeterReadingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    public MeterReadingServiceImpl(
            MeterReadingRepository meterReadingRepository,
            RoomRepository roomRepository,
            BuildingRepository buildingRepository
    ) {
        this.meterReadingRepository = meterReadingRepository;
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    @Transactional
    public void recordMeterReadings(
            Integer buildingId,
            List<MeterReadingDTO> readings
    ) {
        Building building = getBuilding(buildingId);

        validateReadings(readings);

        Map<Integer, Room> rooms = getRooms(readings);

        validateRooms(building, rooms);

        List<MeterReading> meterReadings =
                createMeterReadings(readings, rooms);

        meterReadingRepository.saveAll(meterReadings);
    }


    
    @Override
    public List<MeterReadingDTO> getMeterReadings(
            Integer roomId
    ) {
        return meterReadingRepository
                .findByRoomRoomId(roomId)
                .stream()
                .map(MeterReadingMapper::toDTO)
                .toList();
    }



    private Building getBuilding(Integer buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() ->
                        new RuntimeException("Building not found")
                );
    }



    private Map<Integer, Room> getRooms(
            List<MeterReadingDTO> readings
    ) {
        List<Integer> roomIds = readings.stream()
                .map(MeterReadingDTO::getRoomId)
                .toList();

        return roomRepository.findByRoomIdIn(roomIds)
                .stream()
                .collect(Collectors.toMap(
                        Room::getRoomId,
                        Function.identity()
                ));
    }



    private void validateReadings(
            List<MeterReadingDTO> readings
    ) {
        for (MeterReadingDTO reading : readings) {

            if (reading.getMeterReadingValue() == null) {
                throw new RuntimeException(
                        "Meter reading value is required"
                );
            }

            if (reading.getMeterReadingValue() < 0) {
                throw new RuntimeException(
                        "Meter reading must be non-negative"
                );
            }

            if (reading.getUtilityType() == null) {
                throw new RuntimeException(
                        "Utility type is required"
                );
            }
        }
    }



    private void validateRooms(
            Building building,
            Map<Integer, Room> rooms
    ) {
        for (Room room : rooms.values()) {

            if (!room.getBuilding()
                    .getBuildingId()
                    .equals(building.getBuildingId())) {

                throw new RuntimeException(
                        "Room does not belong to building"
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
            throw new RuntimeException("Room not found");
        }

        return room;
    }
}