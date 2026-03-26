package com.hotelBookingApi.Hotel.booking.API.Controller;


import com.hotelBookingApi.Hotel.booking.API.Dto.*;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDTO> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("{bookingId}/addguests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuest(bookingId , guestDTOList));
    }

    @PostMapping("{bookingId}/payments")
    public ResponseEntity<BookingPaymentInitResponseDTO> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(new BookingPaymentInitResponseDTO(sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponseDTO> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(new BookingStatusResponseDTO(bookingService.getBookingStatus(bookingId)));
    }
}
