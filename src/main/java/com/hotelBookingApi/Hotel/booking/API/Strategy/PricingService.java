package com.hotelBookingApi.Hotel.booking.API.Strategy;

import com.hotelBookingApi.Hotel.booking.API.Entities.InventoryEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculatedynamicPricing(InventoryEntity inventory){
        PricingStrategy pricingStrategy = new BasePriceStrategy();

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPriceStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public BigDecimal calculateTotalPrice(List<InventoryEntity> inventoryLst){
        return inventoryLst.stream()
                .map(this::calculatedynamicPricing)
                .reduce(BigDecimal.ZERO , BigDecimal::add);
    }


}
