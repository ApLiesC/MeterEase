package com.meterease.backend.service;

import com.meterease.backend.dto.MeterReadingDTO;

import java.util.List;

public interface MeterReadingService {

    void recordMeterReadings(
            String managerEmail,
            Integer buildingId,
            List<MeterReadingDTO> readings
    );

    List<MeterReadingDTO> getMeterReadingHistory(
            String managerEmail,
            Integer roomId
    );
}