package com.hotelBookingApi.Hotel.booking.API.Dto;

import lombok.Data;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.math.BigDecimal;

@Data
public class RoomDTO {
    private Long id;
    private String type;
    private BigDecimal basePrice;
    private String[] photos;
    private String[] amenities;
    private Integer  totalCount;
    private Integer capacity;
}
