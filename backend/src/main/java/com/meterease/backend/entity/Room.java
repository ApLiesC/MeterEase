package com.meterease.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    private Integer tenantId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private BigDecimal rentAmount;
}