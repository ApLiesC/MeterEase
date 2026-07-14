package com.meterease.backend.repository;

import com.meterease.backend.entity.MeterReading;
import com.meterease.backend.type.UtilityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository 
        extends JpaRepository<MeterReading, Integer> {

    List<MeterReading> findByRoomRoomId(Integer roomId);

    Optional<MeterReading> findTopByRoomRoomIdAndUtilityTypeOrderByRecordedDateTimeDesc(
            Integer roomId,
            UtilityType utilityType
    );
    List<MeterReading>
    findByRoomRoomIdOrderByRecordedDateTimeDesc(
            Integer roomId
    );
}