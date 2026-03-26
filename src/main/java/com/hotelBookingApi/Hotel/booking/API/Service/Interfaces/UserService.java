package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.ProfileUpdateRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UserDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;

public interface UserService {
    UserEntity getUserById(Long id);
    void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO);
    UserDTO getMyProfile();
}
