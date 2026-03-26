package com.hotelBookingApi.Hotel.booking.API.Service.Interfaces;

import com.hotelBookingApi.Hotel.booking.API.Entities.BookingEntity;

public interface CheckoutService {

    String getCheckoutSession(BookingEntity booking , String successUrl , String failuerUrl);
}
