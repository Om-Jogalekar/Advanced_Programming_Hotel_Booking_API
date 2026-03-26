package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.HotelPriceDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelSearchRequest;
import com.hotelBookingApi.Hotel.booking.API.Dto.InventoryDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UpdateInventoryRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.InventoryEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.RoomEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Repositories.HotelMiniPriceRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.InventoryRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.RoomRepository;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.hotelBookingApi.Hotel.booking.API.Utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMiniPriceRepository hotelMiniPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializeRoomForAYear(RoomEntity room){
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; !today.isAfter(endDate); today=today.plusDays(1)){
            InventoryEntity inventory = InventoryEntity.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(RoomEntity room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage() , hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate()) + 1;
        Page<HotelPriceDTO> hotelPage = hotelMiniPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomsCount(),dateCount,pageable);
        return hotelPage.map((element) -> modelMapper.map(element,HotelPriceDTO.class));
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not Found"));
        UserEntity user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You arre not the owner of room with id: "+roomId);
        }

            return inventoryRepository.findByRoomOrderByDate(room).stream()
                    .map((element) -> modelMapper.map(element, InventoryDTO.class))
                    .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        UserEntity user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: " + roomId);

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDTO.getStartDate(),
                updateInventoryRequestDTO.getEndDate());
        inventoryRepository.updateInventory(roomId,updateInventoryRequestDTO.getStartDate(),updateInventoryRequestDTO.getEndDate(),updateInventoryRequestDTO.getClosed(),updateInventoryRequestDTO.getSurgeFactor());
    }
}
