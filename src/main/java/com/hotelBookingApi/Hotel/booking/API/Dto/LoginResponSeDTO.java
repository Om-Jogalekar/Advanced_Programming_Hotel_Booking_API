package com.hotelBookingApi.Hotel.booking.API.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.AjAttribute;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponSeDTO {
    private String accessToken;
}

