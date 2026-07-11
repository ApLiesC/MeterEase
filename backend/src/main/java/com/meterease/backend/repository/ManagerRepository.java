package com.meterease.backend.repository;

import com.meterease.backend.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);

}