package com.meterease.backend.service;

import com.meterease.backend.dto.MeterOcrResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeterOcrService {

    MeterOcrResponse scanMeterImage(
            MultipartFile image
    );
}