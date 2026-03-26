package com.hotelBookingApi.Hotel.booking.API.Service;


import com.hotelBookingApi.Hotel.booking.API.Entities.HotelEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.HotelMinPriceEntity;
import com.hotelBookingApi.Hotel.booking.API.Entities.InventoryEntity;
import com.hotelBookingApi.Hotel.booking.API.Repositories.HotelMiniPriceRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.HotelRepository;
import com.hotelBookingApi.Hotel.booking.API.Repositories.InventoryRepository;
import com.hotelBookingApi.Hotel.booking.API.Strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMiniPriceRepository hotelMiniPriceRepository;
    private final PricingService pricingService;

    @Component
    @RequiredArgsConstructor
    public class TestRunner implements CommandLineRunner {
        private final PricingUpdateService pricingUpdateService;

        @Override
        public void run(String... args) {
            pricingUpdateService.updatePrices(); // runs once when app starts
        }
    }


    @Scheduled(cron = "*/5 * * * * *")
    public void updatePrices(){
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<HotelEntity> hotelPage = hotelRepository.findAll(PageRequest.of(page , batchSize));
            if(hotelPage.isEmpty()) {
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrice);

            page++;
        }
    }

    private void updateHotelPrice(HotelEntity hotel){

        log.info("Updating prices for hotel: {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        List<InventoryEntity> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel , startDate , endDate);

        updateInventoryPrices(inventoryList);
        log.info("Inventory count for hotel {}: {}", hotel.getId(), inventoryList.size());

        updateHotelMinPrice(hotel , inventoryList , startDate , endDate);
        log.info("Finished updating hotel: {}", hotel.getId());
    }

    private void updateHotelMinPrice(HotelEntity hotel, List<InventoryEntity> inventoryList , LocalDate startDate , LocalDate endDate){

        Map<LocalDate,BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        InventoryEntity::getDate,
                        Collectors.mapping(InventoryEntity::getPrice,Collectors.minBy(Comparator.naturalOrder()))
                ))      .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMinPriceEntity> hotelPrices =new ArrayList<>();
        dailyMinPrices.forEach((date , price) ->{
            HotelMinPriceEntity hotelPrice = hotelMiniPriceRepository.findByHotelAndDate(hotel , date)
                    .orElse(new HotelMinPriceEntity(hotel,date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        hotelMiniPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<InventoryEntity> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculatedynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
