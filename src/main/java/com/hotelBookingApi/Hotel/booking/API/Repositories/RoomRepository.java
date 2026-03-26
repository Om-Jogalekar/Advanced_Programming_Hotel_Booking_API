package com.hotelBookingApi.Hotel.booking.API.Repositories;

import com.hotelBookingApi.Hotel.booking.API.Entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity , Long>{
}

