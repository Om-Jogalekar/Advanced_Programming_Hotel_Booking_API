package com.hotelBookingApi.Hotel.booking.API.Security;

import com.hotelBookingApi.Hotel.booking.API.Dto.LoginDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.SignUpRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UserDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Enums.Role;
import com.hotelBookingApi.Hotel.booking.API.Exceptions.ResourceNotFoundException;
import com.hotelBookingApi.Hotel.booking.API.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO){
        UserEntity user = userRepository.findByEmail(signUpRequestDTO.getEmail()).orElse(null);

        if(user != null){
            throw new RuntimeException("User is already present with same email id");
        }

        UserEntity newUser = modelMapper.map(signUpRequestDTO , UserEntity.class);
        if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
            newUser.setRoles(Set.of(Role.GUEST));
        }
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser , UserDTO.class);
    }

    public String[] login(LoginDTO loginDTO){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),loginDTO.getPassword()
        ));

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateAccessToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        UserEntity user =userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found withid: "+id));
        return jwtService.generateAccessToken(user);
    }

}
