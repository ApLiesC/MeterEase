package com.meterease.backend.controller;


import com.meterease.backend.dto.MeterReadingDTO;
import com.meterease.backend.service.MeterReadingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/buildings/{buildingId}/meter-readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(
            MeterReadingService meterReadingService
    ) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping
    public void recordMeterReadings(
            @PathVariable Integer buildingId,
            @RequestBody List<MeterReadingDTO> readings
    ) {

        meterReadingService.recordMeterReadings(
                buildingId,
                readings
        );
    }
}