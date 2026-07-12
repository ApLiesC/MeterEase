package com.meterease.backend.dto;

import com.meterease.backend.type.BillingMethod;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingSettingsDTO {

    private Integer settingId;

    private Integer buildingId;

    private BillingMethod electricityBillingMethod;

    private BigDecimal electricityRatePerUnit;

    private BigDecimal electricityFlatFeeAmount;

    private BillingMethod waterBillingMethod;

    private BigDecimal waterRatePerUnit;

    private BigDecimal waterFlatFeeAmount;

    private Integer dueDatePeriodDays;

    private BigDecimal dailyLateFeeAmount;
}