package com.meterease.backend.dto;

import com.meterease.backend.entity.Manager;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagerResponse {

    private Integer managerId;

    private String fullName;

    private String emailAddress;

    public static ManagerResponse fromEntity(Manager manager) {

        return ManagerResponse.builder()
                .managerId(manager.getManagerId())
                .fullName(manager.getFullName())
                .emailAddress(manager.getEmailAddress())
                .build();

    }

}