package com.hotelBookingApi.Hotel.booking.API.Strategy;

import com.hotelBookingApi.Hotel.booking.API.Entities.InventoryEntity;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(InventoryEntity inventory);
}
