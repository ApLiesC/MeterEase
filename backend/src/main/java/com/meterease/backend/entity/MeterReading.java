package com.meterease.backend.entity;

import com.meterease.backend.type.UtilityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "meter_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer meterReadingId;


    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UtilityType utilityType;


    @Column(nullable = false)
    @Min(0)
    @Max(99999)
    private Integer meterReadingValue;


    private String meterImage;


    @Column(nullable = false)
    private LocalDateTime recordedDateTime;

}