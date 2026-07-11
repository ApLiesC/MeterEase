package com.meterease.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "manager",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email_address")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long managerId;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(nullable = false)
    private String password;
}