package com.meterease.backend.mapper;

import com.meterease.backend.dto.MeterReadingDTO;
import com.meterease.backend.entity.MeterReading;

public class MeterReadingMapper {


    public static MeterReadingDTO toDTO(
            MeterReading reading
    ) {

        return MeterReadingDTO.builder()

                .meterReadingId(
                        reading.getMeterReadingId()
                )

                .roomId(
                        reading.getRoom().getRoomId()
                )

                .utilityType(
                        reading.getUtilityType()
                )

                .meterReadingValue(
                        reading.getMeterReadingValue()
                )

                .meterImage(
                        reading.getMeterImage()
                )

                .recordedDateTime(
                        reading.getRecordedDateTime()
                )

                .build();

    }

}