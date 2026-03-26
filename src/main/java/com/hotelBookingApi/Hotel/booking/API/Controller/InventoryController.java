package com.hotelBookingApi.Hotel.booking.API.Controller;

import com.hotelBookingApi.Hotel.booking.API.Dto.InventoryDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UpdateInventoryRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.InventoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId , @RequestBody UpdateInventoryRequestDTO updateInventoryRequestDTO){
        inventoryService.updateInventory(roomId , updateInventoryRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
