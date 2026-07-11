package com.meterease.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer buildingId;

    @Column(nullable = false)
    private Integer managerId;

    @NotBlank
    @Column(nullable = false)
    private String buildingName;

    @NotBlank
    @Column(nullable = false)
    private String address;
}