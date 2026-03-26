package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelInfoDto;


import java.util.List;

public interface HotelService {
    HotelDTO createnewHotel(HotelDTO hotelDTO);
    HotelDTO getHotelById(Long id);
    HotelDTO updateHotelById(Long id , HotelDTO hotelDto);
    void deleteHotelById(Long id);
    void activeHotel(Long hotelId);
    HotelInfoDto getHotelInfoById(Long hotelId);
    List<HotelDTO> getAllHotels();
}
