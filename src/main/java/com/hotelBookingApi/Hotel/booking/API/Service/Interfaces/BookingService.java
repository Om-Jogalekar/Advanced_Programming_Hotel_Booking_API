package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.BookingDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.BookingRequest;
import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelReportDTO;
import com.hotelBookingApi.Hotel.booking.API.Enums.BookingStatus;
import com.stripe.model.Event;


import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDTO initialiseBooking(BookingRequest bookingRequest);

    BookingDTO addGuest(Long bookingId , List<GuestDTO> guestDTOList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);

    List<BookingDTO> getAllBookingByHotel(Long bookingId);

    HotelReportDTO getHotelReport(Long hotelId , LocalDate stratDate , LocalDate endDate );

    List<BookingDTO> getMyBookings();
}
