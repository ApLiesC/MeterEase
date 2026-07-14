package com.meterease.backend.controller;

import com.meterease.backend.dto.MeterOcrResponse;
import com.meterease.backend.service.MeterOcrService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/meter-ocr")
public class MeterOcrController {

    private final MeterOcrService meterOcrService;

    public MeterOcrController(
            MeterOcrService meterOcrService
    ) {
        this.meterOcrService = meterOcrService;
    }

    @PostMapping(
            value = "/scan",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public MeterOcrResponse scanMeterImage(
            @RequestPart("image")
            MultipartFile image
    ) {
        return meterOcrService.scanMeterImage(
                image
        );
    }
}