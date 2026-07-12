package com.meterease.backend.controller;

import com.meterease.backend.dto.GenerateRoomsRequest;
import com.meterease.backend.dto.RoomDTO;
import com.meterease.backend.service.RoomService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomDTO> getRooms(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Integer buildingId
    ) {
        return roomService.getRooms(
                jwt.getSubject(),
                buildingId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDTO createRoom(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RoomDTO roomDTO
    ) {
        return roomService.createRoom(
                jwt.getSubject(),
                roomDTO
        );
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RoomDTO> generateRooms(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody GenerateRoomsRequest request
    ) {
        return roomService.generateRooms(
                jwt.getSubject(),
                request
        );
    }

    @PutMapping("/{roomId}")
    public RoomDTO updateRoom(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer roomId,
            @Valid @RequestBody RoomDTO roomDTO
    ) {
        return roomService.updateRoom(
                jwt.getSubject(),
                roomId,
                roomDTO
        );
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer roomId
    ) {
        roomService.deleteRoom(
                jwt.getSubject(),
                roomId
        );
    }
}