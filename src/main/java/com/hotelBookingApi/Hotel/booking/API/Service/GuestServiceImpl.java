package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.GuestEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Repositories.GuestRepository;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.GuestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.hotelBookingApi.Hotel.booking.API.Utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDTO> getAllGuests() {
        UserEntity user = getCurrentUser();
        log.info("Fetching all guests of user with id: {}" , user.getId());
        List<GuestEntity> guests = guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest , GuestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateGuest(Long guestId, GuestDTO guestDTO) {
        log.info("Updating guest with Id: {}" , guestId);
        GuestEntity guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        UserEntity user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");
        modelMapper.map(guestDTO , guest);
        guest.setUser(user);
        guest.setId(guestId);

        guestRepository.save(guest);
        log.info("Guest with id: {} update successfully",guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with id: {}" ,guestId);
        GuestEntity guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));
        UserEntity user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guestRepository.deleteById(guestId);
        log.info("Guest with ID: {} deleted successfully", guestId);
    }

    @Override
    public GuestDTO addNewGuest(GuestDTO guestDTO) {
        log.info("Adding new guest: {}" , guestDTO);
        UserEntity user = getCurrentUser();
        GuestEntity guest = modelMapper.map(guestDTO , GuestEntity.class);
        guest.setUser(user);
        GuestEntity saveGuest = guestRepository.save(guest);
        log.info("Guest added with id : {}" , saveGuest.getId());
        return modelMapper.map(saveGuest , GuestDTO.class);
    }
}
