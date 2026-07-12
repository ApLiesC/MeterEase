package com.meterease.backend.repository;

import com.meterease.backend.entity.AdditionalCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdditionalChargeRepository 
        extends JpaRepository<AdditionalCharge, Integer> {

    List<AdditionalCharge> findByRoomRoomId(Integer roomId);

}