package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelPriceDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelSearchRequest;
import com.hotelBookingApi.Hotel.booking.API.Dto.InventoryDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UpdateInventoryRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.RoomEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(RoomEntity room);

    void deleteAllInventories(RoomEntity room);

    Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDTO> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId , UpdateInventoryRequestDTO updateInventoryRequestDTO);
}
