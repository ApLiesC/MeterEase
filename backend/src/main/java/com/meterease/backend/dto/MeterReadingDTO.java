package com.meterease.backend.dto;

import com.meterease.backend.type.UtilityType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReadingDTO {

    private Integer meterReadingId;

    private Integer roomId;

    private UtilityType utilityType;

    private Integer meterReadingValue;

    private String meterImage;

    private LocalDateTime recordedDateTime;

}