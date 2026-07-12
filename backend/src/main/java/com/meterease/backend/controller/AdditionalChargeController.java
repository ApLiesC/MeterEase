package com.meterease.backend.controller;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.service.AdditionalChargeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/additional-charges")
public class AdditionalChargeController {

    private final AdditionalChargeService service;

    public AdditionalChargeController(
            AdditionalChargeService service
    ) {
        this.service = service;
    }


    @GetMapping
    public List<AdditionalChargeDTO> getCharges(
            @RequestParam(required = false) Integer roomId
    ) {
        return service.getAdditionalCharges(roomId);
    }


    @PostMapping
    public AdditionalChargeDTO createCharge(
            @Valid @RequestBody AdditionalChargeDTO dto
    ) {
        return service.createAdditionalCharge(dto);
    }


    @PutMapping("/{id}")
    public AdditionalChargeDTO updateCharge(
            @PathVariable Integer id,
            @Valid @RequestBody AdditionalChargeDTO dto
    ) {
        return service.updateAdditionalCharge(id, dto);
    }


    @DeleteMapping("/{id}")
    public void deleteCharge(
            @PathVariable Integer id
    ) {
        service.deleteAdditionalCharge(id);
    }
}