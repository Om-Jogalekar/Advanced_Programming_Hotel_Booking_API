package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.ProfileUpdateRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UserDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Repositories.UserRepository;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.hotelBookingApi.Hotel.booking.API.Utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserEntity getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with the id:" + id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO){
        UserEntity user = getCurrentUser();

        if (profileUpdateRequestDTO.getDateOfBirth() != null) user.setDateOfBirth(profileUpdateRequestDTO.getDateOfBirth());
        if (profileUpdateRequestDTO.getGender() != null) user.setGender(profileUpdateRequestDTO.getGender());
        if (profileUpdateRequestDTO.getName() != null) user.setName(profileUpdateRequestDTO.getName());

        userRepository.save(user);
    }



    @Override
    public UserDTO getMyProfile() {
        UserEntity user = getCurrentUser();
        log.info("Getting the profile for user with id: {}" , user.getId());
        return modelMapper.map(user , UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
