package com.hotelBookingApi.Hotel.booking.API.Controller;

import com.hotelBookingApi.Hotel.booking.API.Dto.LoginDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.LoginResponSeDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.SignUpRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UserDTO;
import com.hotelBookingApi.Hotel.booking.API.Security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        return new ResponseEntity<>(authService.signUp(signUpRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponSeDTO> login(@RequestBody LoginDTO loginDTO , HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse) {
        String[] tokens = authService.login(loginDTO);

        Cookie cookie = new Cookie("refreshToken" , tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponSeDTO(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponSeDTO> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthenticationServiceException("Refresh token not found inside the cookies"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponSeDTO(accessToken));

    }
}
