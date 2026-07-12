package com.meterease.backend.service.impl;

import com.meterease.backend.dto.BuildingSettingsDTO;
import com.meterease.backend.entity.Building;
import com.meterease.backend.entity.BuildingSettings;
import com.meterease.backend.mapper.BuildingSettingsMapper;
import com.meterease.backend.repository.BuildingRepository;
import com.meterease.backend.repository.BuildingSettingsRepository;
import com.meterease.backend.service.BuildingSettingsService;

import org.springframework.stereotype.Service;

@Service
public class BuildingSettingsServiceImpl 
        implements BuildingSettingsService {


    private final BuildingSettingsRepository settingsRepository;
    private final BuildingRepository buildingRepository;


    public BuildingSettingsServiceImpl(
            BuildingSettingsRepository settingsRepository,
            BuildingRepository buildingRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.buildingRepository = buildingRepository;
    }


    @Override
    public BuildingSettingsDTO createBuildingSettings(
            Integer buildingId,
            BuildingSettingsDTO dto
    ) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() ->
                        new RuntimeException("Building not found")
                );


        if(settingsRepository
                .findByBuildingBuildingId(buildingId)
                .isPresent()) {

            throw new RuntimeException(
                    "Building settings already exist"
            );
        }


        BuildingSettings settings = BuildingSettings.builder()

                .building(building)

                .electricityBillingMethod(
                        dto.getElectricityBillingMethod()
                )

                .electricityRatePerUnit(
                        dto.getElectricityRatePerUnit()
                )

                .electricityFlatFeeAmount(
                        dto.getElectricityFlatFeeAmount()
                )

                .waterBillingMethod(
                        dto.getWaterBillingMethod()
                )

                .waterRatePerUnit(
                        dto.getWaterRatePerUnit()
                )

                .waterFlatFeeAmount(
                        dto.getWaterFlatFeeAmount()
                )

                .dueDatePeriodDays(
                        dto.getDueDatePeriodDays()
                )

                .dailyLateFeeAmount(
                        dto.getDailyLateFeeAmount()
                )

                .build();


        BuildingSettings saved =
                settingsRepository.save(settings);


        return BuildingSettingsMapper.toDTO(saved);
    }


    @Override
    public BuildingSettingsDTO getBuildingSettings(
            Integer buildingId
    ) {

        BuildingSettings settings =
                settingsRepository
                .findByBuildingBuildingId(buildingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Building settings not found"
                        )
                );


        return BuildingSettingsMapper.toDTO(settings);
    }



    @Override
    public BuildingSettingsDTO updateBuildingSettings(
            Integer buildingId,
            BuildingSettingsDTO dto
    ) {

        BuildingSettings settings =
                settingsRepository
                .findByBuildingBuildingId(buildingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Building settings not found"
                        )
                );


        settings.setElectricityBillingMethod(
                dto.getElectricityBillingMethod()
        );

        settings.setElectricityRatePerUnit(
                dto.getElectricityRatePerUnit()
        );

        settings.setElectricityFlatFeeAmount(
                dto.getElectricityFlatFeeAmount()
        );


        settings.setWaterBillingMethod(
                dto.getWaterBillingMethod()
        );

        settings.setWaterRatePerUnit(
                dto.getWaterRatePerUnit()
        );

        settings.setWaterFlatFeeAmount(
                dto.getWaterFlatFeeAmount()
        );


        settings.setDueDatePeriodDays(
                dto.getDueDatePeriodDays()
        );

        settings.setDailyLateFeeAmount(
                dto.getDailyLateFeeAmount()
        );


        BuildingSettings updated =
                settingsRepository.save(settings);


        return BuildingSettingsMapper.toDTO(updated);
    }
}