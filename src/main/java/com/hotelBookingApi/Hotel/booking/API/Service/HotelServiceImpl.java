package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelInfoDto;
import com.hotelBookingApi.Hotel.booking.API.Dto.RoomDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.RoomEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Exceptions.UnAuthorisedException;
import com.hotelBookingApi.Hotel.booking.API.Repositories.HotelRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.RoomRepository;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.HotelService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.hotelBookingApi.Hotel.booking.API.Utils.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDTO createnewHotel(HotelDTO hotelDTO) {
        HotelEntity hotel = modelMapper.map(hotelDTO , HotelEntity.class);
        hotel.setActive(false);

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        HotelEntity hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner())) throw new UnAuthorisedException("This user does not own this hotel with id: "+id);

        return modelMapper.map(hotel , HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotelById(Long id , HotelDTO hotelDTO) {
        HotelEntity hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner())) throw new UnAuthorisedException("This user does not own this hotel with id: "+id);

        modelMapper.map(hotelDTO ,hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel , HotelDTO.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        HotelEntity hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        UserEntity user= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner())) throw new UnAuthorisedException("This user does not own this hotel with id: "+id);

        for(RoomEntity room : hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activeHotel(Long hotelId) {
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.equals(hotel.getOwner())) throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);

        hotel.setActive(true);
        for (RoomEntity room : hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        List<RoomDTO> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(hotel , RoomDTO.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel , HotelDTO.class),rooms);
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        UserEntity user = getCurrentUser();
        List<HotelEntity> hotels = hotelRepository.findByOwner(user);
        return hotels
                .stream()
                .map((element) -> modelMapper.map(element,HotelDTO.class))
                .collect(Collectors.toList());
    }
}
