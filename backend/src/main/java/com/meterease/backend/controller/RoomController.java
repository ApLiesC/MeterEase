package com.meterease.backend.controller;

import com.meterease.backend.dto.RoomDTO;
import com.meterease.backend.service.RoomService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) Integer buildingId) {

        return roomService.getRooms(buildingId);
    }

    @PostMapping
    public RoomDTO createRoom(
            @Valid @RequestBody RoomDTO roomDTO) {

        return roomService.createRoom(roomDTO);
    }

    @PutMapping("/{roomId}")
    public RoomDTO updateRoom(
            @PathVariable Integer roomId,
            @Valid @RequestBody RoomDTO roomDTO) {

        return roomService.updateRoom(roomId, roomDTO);
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable Integer roomId) {

        roomService.deleteRoom(roomId);
    }
}