package com.hotelBookingApi.Hotel.booking.API.Dto;

import com.hotelBookingApi.Hotel.booking.API.Enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {
    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
}
