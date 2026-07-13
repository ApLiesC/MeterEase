package com.meterease.backend.service;

import com.meterease.backend.dto.MeterReadingDTO;

import java.util.List;

public interface MeterReadingService {

    void recordMeterReadings(
            Integer buildingId,
            List<MeterReadingDTO> readings
    );


    List<MeterReadingDTO> getMeterReadings(
            Integer roomId
    );

}