package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.RoomDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.RoomEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Exceptions.UnAuthorisedException;
import com.hotelBookingApi.Hotel.booking.API.Repositories.HotelRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.RoomRepository;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.InventoryService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO){
        log.info("Creating a new room in hotel with id : {}" , hotelId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel Not Found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not owe this hotel with id: "+hotelId);
        }
        RoomEntity room = modelMapper.map(roomDTO ,RoomEntity.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room , RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId){
        log.info("Getting all rooms in hotel with ID: " , hotelId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not owe this hotel with id: "+hotelId);
        }

        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element , RoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public  RoomDTO getRoomById(Long roomId){
        log.info("Getting the room with Id : {}" , roomId);
        RoomEntity room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found"));
        return modelMapper.map(room , RoomDTO.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId){
        log.info("Deleting the room with the Id: {}" , roomId);
        RoomEntity room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This user does not owe this room with id : " + roomId);
        }

        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    @Transactional
    public RoomDTO updateRoomById(Long hotelId , Long roomId , RoomDTO roomDTO){
        log.info("Updating the room with id: {}" , roomId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new RuntimeException("Hotel not Found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not owe this room with id : " + roomId);
        }

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        modelMapper.map(roomDTO , room);
        room.setId(roomId);
        room = roomRepository.save(room);

        return modelMapper.map(room , RoomDTO.class);
    }
}
