package com.hotelBookingApi.Hotel.booking.API.Repositories;

import com.hotelBookingApi.Hotel.booking.API.Entities.BookingEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

    Optional<BookingEntity> findByPaymentSessionId(String sessionId);

    List<BookingEntity> findByHotel(HotelEntity hotel);

    List<BookingEntity> findByHotelAndCreatedAtBetween(HotelEntity hotel , LocalDateTime stratDate, LocalDateTime endDate);

    List<BookingEntity> findByUser(UserEntity user);
}
