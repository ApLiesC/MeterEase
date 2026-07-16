package com.meterease.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer buildingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String buildingName;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String address;

    @OneToOne(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private BuildingSettings buildingSettings;

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms = new ArrayList<>();
}