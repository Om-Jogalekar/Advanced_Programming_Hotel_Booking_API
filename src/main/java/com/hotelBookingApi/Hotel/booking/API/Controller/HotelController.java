package com.hotelBookingApi.Hotel.booking.API.Controller;

import com.hotelBookingApi.Hotel.booking.API.Dto.BookingDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelReportDTO;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.BookingService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO){
        HotelDTO hotel = hotelService.createnewHotel(hotelDTO);
        return new ResponseEntity<>(hotel , HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long hotelId){

        HotelDTO hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);

    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotelById(@PathVariable Long hotelId , @RequestBody HotelDTO hotelDTO){
        HotelDTO hotel = hotelService.updateHotelById(hotelId , hotelDTO);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId){
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{hotelId}/active")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId){
        hotelService.activeHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings(@PathVariable Long hotelId){
        return ResponseEntity.ok(bookingService.getAllBookingByHotel(hotelId));
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDTO> getHotelReport(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }

}
