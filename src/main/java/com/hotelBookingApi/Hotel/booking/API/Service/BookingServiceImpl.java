package com.hotelBookingApi.Hotel.booking.API.Service;

import com.hotelBookingApi.Hotel.booking.API.Dto.BookingDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.BookingRequest;
import com.hotelBookingApi.Hotel.booking.API.Dto.GuestDTO;
import com.hotelBookingApi.Hotel.booking.API.Dto.HotelReportDTO;
import com.hotelBookingApi.Hotel.booking.API.Entities.*;
import com.hotelBookingApi.Hotel.booking.API.Enums.BookingStatus;
import com.hotelBookingApi.Hotel.booking.API.Exceptions.ResourceNotFoundException;
import com.hotelBookingApi.Hotel.booking.API.Exceptions.UnAuthorisedException;
import com.hotelBookingApi.Hotel.booking.API.Repositories.*;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.BookingService;
import com.hotelBookingApi.Hotel.booking.API.Service.Interfaces.CheckoutService;
import com.hotelBookingApi.Hotel.booking.API.Strategy.PricingService;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.hotelBookingApi.Hotel.booking.API.Utils.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    private static final boolean TEST_MODE = true;

    private String url;

    @Override
    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {
        HotelEntity hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() -> new RuntimeException("Hotel Not Found"));
        RoomEntity room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() -> new RuntimeException("Room Not Found"));
        List<InventoryEntity> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if (inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        inventoryRepository.initBooking(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        BookingEntity booking = BookingEntity.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOut(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();
        booking =bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO addGuest(Long bookingId, List<GuestDTO> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);

        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));

        UserEntity user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDTO guestDto: guestDtoList) {
            GuestEntity guest = modelMapper.map(guestDto, GuestEntity.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }


    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(()->new RuntimeException("Booking not found with id: "+ bookingId));
        UserEntity user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new RuntimeException("Booking does not belong to this user with id: " + user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

        //By passed the payment method for now
        if(TEST_MODE){
            booking.setBookingStatus(BookingStatus.CONFIRMED);

            inventoryRepository.findAndLockReservedInventory(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOut(),
                    booking.getRoomsCount()
            );

            inventoryRepository.confirmBooking(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOut(),
                    booking.getRoomsCount()

            );

            bookingRepository.save(booking);

            return "TEST_PAYMENT_SUCCESS";
        }

        //to do integrate payment
        String sessionUrl = checkoutService.getCheckoutSession(booking , url+"/payments/"+ bookingId + "/status" , url+"/payments/"+ bookingId + "/status");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            BookingEntity booking = bookingRepository.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("Booking not found for session Id : "+ sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId() , booking.getCheckInDate(),booking.getCheckOut(),booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId() , booking.getCheckInDate() , booking.getCheckOut() , booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}",booking.getId());
        }
        else {
            log.warn("Unhandled event type: {}", event.getType());
        }

    }

    @Override
    public void cancelBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> new RuntimeException("Booking not found with id: "+bookingId));
        UserEntity user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOut(),
                booking.getRoomsCount()
        );

        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOut(),
                booking.getRoomsCount());

        try{
            if(!TEST_MODE) {
                Session session = Session.retrieve(booking.getPaymentSessionId());
                RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                        .setPaymentIntent(session.getPaymentIntent())
                        .build();
                Refund.create(refundCreateParams);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: "+bookingId));
        UserEntity user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }
        return booking.getBookingStatus();
    }

    @Override
    public List<BookingDTO> getAllBookingByHotel(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not " +
                        "found with ID: "+hotelId));
        UserEntity user = getCurrentUser();
        if(!user.equals(hotel.getOwner())) throw new RuntimeException("You are not the owner of hotel with id: "+hotelId);

        List<BookingEntity> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element , BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        HotelEntity hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        UserEntity user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new RuntimeException("You are not the owner of hotel with id: "+hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<BookingEntity> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(BookingEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDTO(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    @Override
    public List<BookingDTO> getMyBookings() {
        UserEntity user = getCurrentUser();

        return bookingRepository.findByUser(user)
                .stream()
                .map((element) -> modelMapper.map(element,BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    @Transactional
    public void deleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<BookingEntity> expiredBookings = bookingRepository.findAll().stream()
                .filter(this::hasBookingExpired)
                .toList();

        bookingRepository.deleteAll(expiredBookings);
    }

    public boolean hasBookingExpired(BookingEntity booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
