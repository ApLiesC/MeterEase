package com.meterease.backend.mapper;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.entity.AdditionalCharge;

public class AdditionalChargeMapper {

    public static AdditionalChargeDTO toDTO(AdditionalCharge charge) {

        AdditionalChargeDTO dto = new AdditionalChargeDTO();

        dto.setAdditionalChargeId(
                charge.getAdditionalChargeId()
        );

        dto.setRoomId(
                charge.getRoom().getRoomId()
        );

        dto.setChargeName(
                charge.getChargeName()
        );

        dto.setChargeAmount(
                charge.getChargeAmount()
        );

        return dto;
    }
}