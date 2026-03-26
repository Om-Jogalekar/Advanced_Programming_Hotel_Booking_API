package com.hotelBookingApi.Hotel.booking.API.Utils;

import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AppUtils {

    public static UserEntity getCurrentUser(){
        return (UserEntity) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
