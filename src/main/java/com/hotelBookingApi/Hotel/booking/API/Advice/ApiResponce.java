package com.hotelBookingApi.Hotel.booking.API.Advice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponce<T>{

    private LocalDateTime timeStamp;
    private T data;
    private ApiError error;

    public ApiResponce(){
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponce(T data){
        this();
        this.data = data;
    }

    public ApiResponce(ApiError error){
        this();
        this.error = error;
    }
}
