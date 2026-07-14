package com.meterease.backend.controller;

import com.meterease.backend.dto.MeterReadingDTO;
import com.meterease.backend.service.MeterReadingService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(
            MeterReadingService meterReadingService
    ) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/buildings/{buildingId}/meter-readings")
    @ResponseStatus(HttpStatus.CREATED)
    public void recordMeterReadings(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer buildingId,
            @RequestBody List<MeterReadingDTO> readings
    ) {
        meterReadingService.recordMeterReadings(
                jwt.getSubject(),
                buildingId,
                readings
        );
    }

    @GetMapping("/rooms/{roomId}/meter-readings")
    public List<MeterReadingDTO> getMeterReadingHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer roomId
    ) {
        return meterReadingService.getMeterReadingHistory(
                jwt.getSubject(),
                roomId
        );
    }
}