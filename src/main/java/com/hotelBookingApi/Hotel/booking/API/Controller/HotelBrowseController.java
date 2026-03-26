package com.hotelBookingApi.Hotel.booking.API.Controller;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelInfoDto;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelPriceDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelSearchRequest;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.HotelService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){

        Page<HotelPriceDTO> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
