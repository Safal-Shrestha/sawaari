package com.sawari.dev.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.Booking;
import com.sawari.dev.model.Parking;
import com.sawari.dev.model.Slot;
import com.sawari.dev.model.Vehicle;
import com.sawari.dev.model.dto.BookingDTO;
import com.sawari.dev.model.dto.CreateBookingRequest;
import com.sawari.dev.repository.BookingRepository;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.repository.SlotRepository;
import com.sawari.dev.repository.VehicleRepository;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;

    private static final BigDecimal TWO_WHEELER_RATE = new BigDecimal("20.00");
    private static final BigDecimal FOUR_WHEELER_RATE = new BigDecimal("50.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    // Nepal timezone
    private static final ZoneId NEPAL_TIMEZONE = ZoneId.of("Asia/Kathmandu");

    public BookingService(BookingRepository bookingRepository,
                          SlotRepository slotRepository,
                          VehicleRepository vehicleRepository,
                          ParkingRepository parkingRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingRepository = parkingRepository;
    }

    // Helper method to get current time in Nepal timezone
    private LocalDateTime getCurrentNepalTime() {
        return LocalDateTime.now(NEPAL_TIMEZONE);
    }

    // Helper method to convert LocalDateTime to ZonedDateTime in Nepal timezone
    private ZonedDateTime toNepalZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(NEPAL_TIMEZONE);
    }

    @Transactional
    public BookingDTO createBooking(CreateBookingRequest request, Long userId) {
        logger.info("Creating booking for user: {}, parking: {}, vehicle: {}",
                userId, request.getParkingId(), request.getVehicleId());

        // Vehicle validation
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found"));

        // Parse dates
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(request.getExpectedStart());
            endTime = LocalDateTime.parse(request.getExpectedEnd());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected ISO format: yyyy-MM-ddTHH:mm:ss");
        }

        // Convert to Nepal timezone for accurate comparisons
        ZonedDateTime nepalStartTime = toNepalZonedDateTime(startTime);
        ZonedDateTime nepalEndTime = toNepalZonedDateTime(endTime);
        ZonedDateTime currentNepalTime = ZonedDateTime.now(NEPAL_TIMEZONE);

        // Validate times
        if (nepalEndTime.isBefore(nepalStartTime) || nepalEndTime.isEqual(nepalStartTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }
        
        // Check if booking is in the past
        if (nepalStartTime.isBefore(currentNepalTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book in the past");
        }

        // Find available slots for the SPECIFIC time period
        List<Slot> availableSlots = slotRepository.findAvailableSlotsForTimePeriod(
                request.getParkingId(), 
                vehicle.getVModel(), 
                startTime, // Use LocalDateTime for DB query
                endTime);

        if (availableSlots.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "No available slots for vehicle type: " + vehicle.getVModel() + 
                    " during the requested time period: " + startTime + " to " + endTime);
        }

        // CONCURRENT BOOKING FIX: Double-check slot isn't reserved
        Slot slot = null;
        for (Slot s : availableSlots) {
            Slot freshSlot = slotRepository.findById(s.getSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
            
            if (!freshSlot.getIsReserved() && !freshSlot.getIsOccupied()) {
                slot = freshSlot;
                break;
            }
        }
        
        if (slot == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Slot was just reserved by another user. Please try another slot.");
        }

        // Calculate price
        long hours = Duration.between(startTime, endTime).toHours();
        if (hours < 1) hours = 1;

        BigDecimal rate = getRateByVehicleModel(vehicle.getVModel());
        BigDecimal basePrice = rate.multiply(BigDecimal.valueOf(hours));

        // Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setParkingId(request.getParkingId());
        booking.setSlotId(slot.getSlotId());
        booking.setVehicleId(request.getVehicleId());
        booking.setExpectedStartingTime(startTime);
        booking.setExpectedEndTime(endTime);
        booking.setBasePrice(basePrice);
        booking.setFineAmount(BigDecimal.ZERO);
        booking.setTotalAmount(basePrice);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingDateTime(getCurrentNepalTime());

        // Only reserve slot if booking starts within next 30 minutes
        long minutesUntilStart = Duration.between(currentNepalTime, nepalStartTime).toMinutes();
        if (minutesUntilStart <= 30 && minutesUntilStart >= 0) {
            slot.setIsReserved(true);
            slotRepository.save(slot);
            logger.info("Slot {} reserved in Nepal time (starts in {} minutes)", 
                    slot.getSlotId(), minutesUntilStart);
        }

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully in Nepal time: id={}, slot={}, from={} to={}", 
                savedBooking.getBookingId(), slot.getSlotId(), startTime, endTime);

        return convertToDTO(savedBooking);
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        logger.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getActiveBookings(Long userId) {
        logger.info("Fetching active bookings for user: {}", userId);
        return bookingRepository.findByUserIdAndBookingStatus(userId, BookingStatus.CONFIRMED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO checkIn(Long bookingId, Long authenticatedUserId) {
        logger.info("Processing check-in for booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can only check-in to confirmed bookings. Current status: " + booking.getBookingStatus());
        }

        Parking parking = parkingRepository.findById(booking.getParkingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found"));

        // allow booking owner or parking owner to check-in
        if (!booking.getUserId().equals(authenticatedUserId) && !parking.getOwnerId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to check-in this booking");
        }

        // Check timing with Nepal timezone
        ZonedDateTime nepalNow = ZonedDateTime.now(NEPAL_TIMEZONE);
        ZonedDateTime nepalStartTime = toNepalZonedDateTime(booking.getExpectedStartingTime());
        
        if (nepalNow.isBefore(nepalStartTime.minusMinutes(30))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot check-in more than 30 minutes before booking start time");
        }

        if (nepalNow.isAfter(nepalStartTime.plusMinutes(30))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot check-in more than 30 minutes after booking start time");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_IN);

        Slot slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        slot.setIsOccupied(true);
        slot.setIsReserved(false); // Remove reservation, now physically occupied
        slotRepository.save(slot);

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Check-in completed in Nepal time for booking: {}", bookingId);

        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO checkOut(Long bookingId, Long authenticatedUserId) {
        logger.info("Processing check-out for booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can only check-out from checked-in bookings. Current status: " + booking.getBookingStatus());
        }

        Parking parking = parkingRepository.findById(booking.getParkingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found"));

        if (!booking.getUserId().equals(authenticatedUserId) && !parking.getOwnerId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to check-out this booking");
        }

        ZonedDateTime nepalCheckOutTime = ZonedDateTime.now(NEPAL_TIMEZONE);
        ZonedDateTime nepalExpectedEndTime = toNepalZonedDateTime(booking.getExpectedEndTime());

        // Calculate overtime fine
        if (nepalCheckOutTime.isAfter(nepalExpectedEndTime)) {
            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

            long overtimeMinutes = Duration.between(nepalExpectedEndTime, nepalCheckOutTime).toMinutes();
            long overtimeHours = (overtimeMinutes + 59) / 60; // Round up to nearest hour
            
            if (overtimeHours < 1) overtimeHours = 1;

            BigDecimal rate = getRateByVehicleModel(vehicle.getVModel());
            BigDecimal fine = rate.multiply(OVERTIME_MULTIPLIER)
                    .multiply(BigDecimal.valueOf(overtimeHours));

            booking.setFineAmount(fine);
            booking.setTotalAmount(booking.getBasePrice().add(fine));
            logger.info("Overtime fine calculated in Nepal time: {} for {} hours ({} minutes)", 
                    fine, overtimeHours, overtimeMinutes);
        }

        booking.setBookingStatus(BookingStatus.COMPLETED);

        Slot slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        slot.setIsOccupied(false);
        slot.setIsReserved(false);
        slotRepository.save(slot);

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Check-out completed in Nepal time for booking: {}", bookingId);
        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO cancelBooking(Long bookingId, Long authenticatedUserId) {
        logger.info("Cancelling booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getUserId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.COMPLETED ||
            booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel booking with status: " + booking.getBookingStatus());
        }

        if (booking.getBookingStatus() == BookingStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel checked-in booking. Please check-out first.");
        }

        // Check if it's too late to cancel (less than 30 minutes before start)
        ZonedDateTime nepalNow = ZonedDateTime.now(NEPAL_TIMEZONE);
        ZonedDateTime nepalStartTime = toNepalZonedDateTime(booking.getExpectedStartingTime());
        
        if (nepalNow.isAfter(nepalStartTime.minusMinutes(30))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot cancel booking less than 30 minutes before start time");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        // If slot was reserved, free it
        Slot slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
        
        // Check if there are any other upcoming bookings for this slot
        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsForSlot(
                slot.getSlotId(), 
                getCurrentNepalTime());
        
        // Only unreserve if no upcoming bookings within 30 minutes
        boolean hasUpcomingBooking = false;
        for (Booking upcoming : upcomingBookings) {
            if (upcoming.getBookingId().equals(bookingId)) continue; // Skip the one being cancelled
            ZonedDateTime upcomingStart = toNepalZonedDateTime(upcoming.getExpectedStartingTime());
            long minutesUntilUpcoming = Duration.between(nepalNow, upcomingStart).toMinutes();
            if (minutesUntilUpcoming <= 30 && minutesUntilUpcoming >= 0) {
                hasUpcomingBooking = true;
                break;
            }
        }
        
        if (!hasUpcomingBooking) {
            slot.setIsReserved(false);
            slotRepository.save(slot);
            logger.info("Slot {} unreserved after cancellation", slot.getSlotId());
        }

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking cancelled successfully in Nepal time: {}", bookingId);
        return convertToDTO(savedBooking);
    }

    public BookingDTO getBooking(Long bookingId, Long authenticatedUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Parking parking = parkingRepository.findById(booking.getParkingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found"));

        if (!booking.getUserId().equals(authenticatedUserId) && !parking.getOwnerId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this booking");
        }

        return convertToDTO(booking);
    }

    // Get available slots with time parameters
    public List<Slot> getAvailableSlots(Long parkingId, String vehicleType, 
                                       LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Fetching available slots for parking: {}, vehicle type: {}, from {} to {}", 
                parkingId, vehicleType, startTime, endTime);

        VehicleModel vehicleModel;
        try {
            vehicleModel = VehicleModel.valueOf(vehicleType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid vehicle type: " + vehicleType + ". Valid types: TWO_WHEELER, FOUR_WHEELER");
        }

        // Validate time parameters in Nepal timezone
        ZonedDateTime nepalStartTime = toNepalZonedDateTime(startTime);
        ZonedDateTime nepalEndTime = toNepalZonedDateTime(endTime);
        ZonedDateTime currentNepalTime = ZonedDateTime.now(NEPAL_TIMEZONE);
        
        if (nepalStartTime.isBefore(currentNepalTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Start time cannot be in the past");
        }
        
        if (nepalEndTime.isBefore(nepalStartTime) || nepalEndTime.isEqual(nepalStartTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "End time must be after start time");
        }

        // Use the time-based query to find available slots
        return slotRepository.findAvailableSlotsForTimePeriod(
                parkingId, vehicleModel, startTime, endTime);
    }

    // Keep the old method for backward compatibility
    public List<Slot> getAvailableSlots(Long parkingId, String vehicleType) {
        LocalDateTime now = getCurrentNepalTime();
        return getAvailableSlots(parkingId, vehicleType, now, now.plusDays(1));
    }

    private BigDecimal getRateByVehicleModel(VehicleModel vehicleModel) {
        if (vehicleModel == VehicleModel.TWO_WHEELER) {
            return TWO_WHEELER_RATE;
        } else if (vehicleModel == VehicleModel.FOUR_WHEELER) {
            return FOUR_WHEELER_RATE;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown vehicle model: " + vehicleModel);
        }
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setUserId(booking.getUserId());
        dto.setParkingId(booking.getParkingId());
        dto.setSlotId(booking.getSlotId());
        dto.setVehicleId(booking.getVehicleId());
        dto.setBookingDateTime(booking.getBookingDateTime());
        dto.setExpectedStartingTime(booking.getExpectedStartingTime());
        dto.setExpectedEndTime(booking.getExpectedEndTime());
        dto.setBasePrice(booking.getBasePrice());
        dto.setFineAmount(booking.getFineAmount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getBookingStatus());
        return dto;
    }
}