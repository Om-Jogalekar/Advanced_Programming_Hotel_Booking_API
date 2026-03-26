package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;

import java.util.List;

public interface GuestService {
    List<GuestDTO> getAllGuests();

    void updateGuest(Long guestId , GuestDTO guestDTO);

    void deleteGuest(Long guestId);

    GuestDTO addNewGuest(GuestDTO guestDTO);

}
