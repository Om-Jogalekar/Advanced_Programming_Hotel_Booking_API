package com.hotelBookingApi.Hotel.booking.API.Controller;

import com.hotelBookingApi.Hotel.booking.API.Dto.BookingDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.ProfileUpdateRequestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.UserDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.UserEntity;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.BookingService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.GuestService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "User Profile" , description = "Manage user profiles and bookings")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/profile")
    @Operation(summary = "Update my profile" ,description = "Allows a user to update their profile details" , tags = {"User Profile"})
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO){
        userService.updateProfile(profileUpdateRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestDTO>> getAllGuests(){
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    public ResponseEntity<GuestDTO> addNewGuest(@RequestBody GuestDTO guestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDTO));
    }


    @PutMapping("/guests/{guestId}")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId , @RequestBody GuestDTO guestDTO){
        guestService.updateGuest(guestId , guestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId){
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }


}
