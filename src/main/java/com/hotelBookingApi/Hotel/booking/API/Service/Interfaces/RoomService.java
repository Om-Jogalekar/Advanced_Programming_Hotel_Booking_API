package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Dto.RoomDTO;

import java.util.List;

public interface RoomService {
    RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO);

    List<RoomDTO> getAllRoomsInHotel(Long hotelId);

    RoomDTO getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    RoomDTO updateRoomById(Long hotelId , Long roomId , RoomDTO roomDTO);
}
