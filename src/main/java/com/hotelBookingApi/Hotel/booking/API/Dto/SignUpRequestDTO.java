package com.hotelBookingApi.Hotel.booking.API.Dto;

import com.hotelBookingApi.Hotel.booking.API.Enums.Gender;
import com.hotelBookingApi.Hotel.booking.API.Enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class SignUpRequestDTO {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles;
    private LocalDate dateOfBirth;
    private Gender gender;
}
