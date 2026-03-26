package com.hotelBookingApi.Hotel.booking.API.Dto;


import com.hotelBookingApi.Hotel.booking.API.Entities.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDTO {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo hotelContactInfo;
    private Boolean active;
}
