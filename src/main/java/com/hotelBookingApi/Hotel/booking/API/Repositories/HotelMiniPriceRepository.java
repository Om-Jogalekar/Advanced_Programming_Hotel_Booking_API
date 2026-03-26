package com.hotelBookingApi.Hotel.booking.API.Repositories;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelPriceDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelMinPriceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface HotelMiniPriceRepository extends JpaRepository<HotelMinPriceEntity , Long> {

    @Query("""
    SELECT new com.hotelBookingApi.Hotel.booking.API.Dto.HotelPriceDTO(i.hotel, AVG(i.price))
    FROM HotelMinPriceEntity i
    WHERE i.hotel.city = :city
      AND i.date BETWEEN :startDate AND :endDate
      AND i.hotel.active = true
    GROUP BY i.hotel
""")
    Page<HotelPriceDTO> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPriceEntity> findByHotelAndDate(HotelEntity hotel, LocalDate date);
}