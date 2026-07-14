package com.meterease.backend.controller;

import com.meterease.backend.service.GeminiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
public class GeminiTestController {

    private final GeminiService geminiService;

    public GeminiTestController(
            GeminiService geminiService
    ) {
        this.geminiService = geminiService;
    }

    @GetMapping("/api/gemini/test")
    public String test() {

        return geminiService.validateReading(
        """
        01629
        220V
        50Hz
        240202834
        """,
        List.of(
                2016,
                1629,
                2024,
                240202834
        )
);

    }

}