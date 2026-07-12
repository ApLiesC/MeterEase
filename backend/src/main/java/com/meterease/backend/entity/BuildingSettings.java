package com.meterease.backend.entity;

import com.meterease.backend.type.BillingMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "building_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @OneToOne
    @JoinColumn(name = "building_id", nullable = false, unique = true)
    private Building building;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingMethod electricityBillingMethod;

    @DecimalMin("0.0")
    private BigDecimal electricityRatePerUnit;

    @DecimalMin("0.0")
    private BigDecimal electricityFlatFeeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingMethod waterBillingMethod;

    @DecimalMin("0.0")
    private BigDecimal waterRatePerUnit;

    @DecimalMin("0.0")
    private BigDecimal waterFlatFeeAmount;

    @Min(1)
    @Column(nullable = false)
    private Integer dueDatePeriodDays;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal dailyLateFeeAmount;
}