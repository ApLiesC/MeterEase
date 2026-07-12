package com.meterease.backend.service;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.entity.Room;

import java.util.List;

public interface AdditionalChargeService {

    AdditionalChargeDTO createAdditionalCharge(
            AdditionalChargeDTO dto
    );

    AdditionalChargeDTO createAdditionalChargeForRoom(
            AdditionalChargeDTO dto,
            Room room
    );

    List<AdditionalChargeDTO> getAdditionalCharges(
            Integer roomId
    );

    AdditionalChargeDTO updateAdditionalCharge(
            Integer additionalChargeId,
            AdditionalChargeDTO dto
    );

    void deleteAdditionalCharge(
            Integer additionalChargeId
    );
}