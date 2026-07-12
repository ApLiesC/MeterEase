package com.meterease.backend.mapper;

import com.meterease.backend.dto.BuildingSettingsDTO;
import com.meterease.backend.entity.BuildingSettings;

public class BuildingSettingsMapper {

    public static BuildingSettingsDTO toDTO(BuildingSettings settings) {

        return BuildingSettingsDTO.builder()
                .settingId(settings.getSettingId())
                .buildingId(settings.getBuilding().getBuildingId())
                .electricityBillingMethod(settings.getElectricityBillingMethod())
                .electricityRatePerUnit(settings.getElectricityRatePerUnit())
                .electricityFlatFeeAmount(settings.getElectricityFlatFeeAmount())
                .waterBillingMethod(settings.getWaterBillingMethod())
                .waterRatePerUnit(settings.getWaterRatePerUnit())
                .waterFlatFeeAmount(settings.getWaterFlatFeeAmount())
                .dueDatePeriodDays(settings.getDueDatePeriodDays())
                .dailyLateFeeAmount(settings.getDailyLateFeeAmount())
                .build();
    }
}