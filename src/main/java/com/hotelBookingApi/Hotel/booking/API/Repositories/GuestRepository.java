package com.hotelBookingApi.Hotel.booking.API.Repositories;

import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.GuestEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity , Long> {
    List<GuestEntity> findByUser(UserEntity user);
}
