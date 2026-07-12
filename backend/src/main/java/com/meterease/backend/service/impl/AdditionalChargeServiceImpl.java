package com.meterease.backend.service.impl;

import com.meterease.backend.dto.AdditionalChargeDTO;
import com.meterease.backend.entity.AdditionalCharge;
import com.meterease.backend.entity.Room;
import com.meterease.backend.mapper.AdditionalChargeMapper;
import com.meterease.backend.repository.AdditionalChargeRepository;
import com.meterease.backend.repository.RoomRepository;
import com.meterease.backend.service.AdditionalChargeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdditionalChargeServiceImpl 
        implements AdditionalChargeService {


    private final AdditionalChargeRepository additionalChargeRepository;
    private final RoomRepository roomRepository;


    public AdditionalChargeServiceImpl(
            AdditionalChargeRepository additionalChargeRepository,
            RoomRepository roomRepository
    ) {
        this.additionalChargeRepository = additionalChargeRepository;
        this.roomRepository = roomRepository;
    }


    // Used when creating a charge separately
    @Override
    public AdditionalChargeDTO createAdditionalCharge(
            AdditionalChargeDTO dto
    ) {

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );


        AdditionalCharge charge =
                createChargeEntity(dto, room);


        AdditionalCharge saved =
                additionalChargeRepository.save(charge);


        return AdditionalChargeMapper.toDTO(saved);
    }


    // Used when creating a room with charges
    @Override
    public AdditionalChargeDTO createAdditionalChargeForRoom(
            AdditionalChargeDTO dto,
            Room room
    ) {

        AdditionalCharge charge =
                createChargeEntity(dto, room);


        AdditionalCharge saved =
                additionalChargeRepository.save(charge);


        return AdditionalChargeMapper.toDTO(saved);
    }


    private AdditionalCharge createChargeEntity(
            AdditionalChargeDTO dto,
            Room room
    ) {

        AdditionalCharge charge =
                new AdditionalCharge();

        charge.setRoom(room);
        charge.setChargeName(dto.getChargeName());
        charge.setChargeAmount(dto.getChargeAmount());

        return charge;
    }


    @Override
    public List<AdditionalChargeDTO> getAdditionalCharges(
            Integer roomId
    ) {

        List<AdditionalCharge> charges;


        if (roomId == null) {

            charges =
                    additionalChargeRepository.findAll();

        } else {

            charges =
                    additionalChargeRepository
                            .findByRoomRoomId(roomId);

        }


        return charges.stream()
                .map(AdditionalChargeMapper::toDTO)
                .toList();
    }


    @Override
    public AdditionalChargeDTO updateAdditionalCharge(
            Integer id,
            AdditionalChargeDTO dto
    ) {

        AdditionalCharge charge =
                additionalChargeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Charge not found")
                );


        charge.setChargeName(
                dto.getChargeName()
        );

        charge.setChargeAmount(
                dto.getChargeAmount()
        );


        AdditionalCharge updated =
                additionalChargeRepository.save(charge);


        return AdditionalChargeMapper.toDTO(updated);
    }


    @Override
    public void deleteAdditionalCharge(
            Integer id
    ) {

        if (!additionalChargeRepository.existsById(id)) {
            throw new RuntimeException("Charge not found");
        }

        additionalChargeRepository.deleteById(id);
    }
}