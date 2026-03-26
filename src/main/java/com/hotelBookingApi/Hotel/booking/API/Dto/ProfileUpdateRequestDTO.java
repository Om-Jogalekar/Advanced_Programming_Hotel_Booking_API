package com.hotelBookingApi.Hotel.booking.API.Dto;

import com.hotelBookingApi.Hotel.booking.API.Enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;

}
