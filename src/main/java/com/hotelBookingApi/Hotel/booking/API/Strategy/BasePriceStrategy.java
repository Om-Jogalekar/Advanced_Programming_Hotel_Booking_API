package com.hotelBookingApi.Hotel.booking.API.Strategy;

import com.hotelBookingApi.Hotel.booking.API.Entities.InventoryEntity;

import java.math.BigDecimal;

public class BasePriceStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(InventoryEntity inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
