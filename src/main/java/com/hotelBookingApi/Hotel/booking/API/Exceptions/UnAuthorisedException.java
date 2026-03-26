package com.hotelBookingApi.Hotel.booking.API.Exceptions;

public class UnAuthorisedException extends RuntimeException{
    public UnAuthorisedException(String message){
        super(message);
    }
}
