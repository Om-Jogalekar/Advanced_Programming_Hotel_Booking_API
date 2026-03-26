package com.hotelBookingApi.Hotel.booking.API.Dto;

import com.hotelBookingApi.Hotel.booking.API.Enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusResponseDTO {
    private BookingStatus bookingStatus;
}
