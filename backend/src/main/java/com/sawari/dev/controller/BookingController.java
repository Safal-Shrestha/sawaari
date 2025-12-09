package com.sawari.dev.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Slot;
import com.sawari.dev.model.dto.BookingDTO;
import com.sawari.dev.model.dto.CreateBookingRequest;
import com.sawari.dev.service.BookingService;
import com.sawari.dev.service.CustomUserDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Validated
public class BookingController {

    private final BookingService bookingService;
    
    // Nepal timezone
    private static final ZoneId NEPAL_TIMEZONE = ZoneId.of("Asia/Kathmandu");

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Helper: get authenticated user id
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Long userId = getAuthenticatedUserId();
        BookingDTO booking = bookingService.createBooking(request, userId);
        return ResponseEntity.status(201).body(booking);
    }

    @GetMapping("/getUserBookings")
    public ResponseEntity<List<BookingDTO>> getUserBookings() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/getActiveBookings")
    public ResponseEntity<List<BookingDTO>> getActiveBookings() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(bookingService.getActiveBookings(userId));
    }

    @PostMapping("/checkIn")
    public ResponseEntity<BookingDTO> checkIn(
            @RequestParam("booking_id") @NotNull(message = "Booking ID is required") Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingDTO dto = bookingService.checkIn(bookingId, userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/checkOut")
    public ResponseEntity<BookingDTO> checkOut(
            @RequestParam("booking_id") @NotNull(message = "Booking ID is required") Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingDTO dto = bookingService.checkOut(bookingId, userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/cancelBooking")
    public ResponseEntity<BookingDTO> cancelBooking(
            @RequestParam("booking_id") @NotNull(message = "Booking ID is required") Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingDTO dto = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getBooking")
    public ResponseEntity<BookingDTO> getBooking(
            @RequestParam("booking_id") @NotNull(message = "Booking ID is required") Long bookingId) {
        Long userId = getAuthenticatedUserId();
        BookingDTO dto = bookingService.getBooking(bookingId, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getAvailableSlots")
    public ResponseEntity<List<Slot>> getAvailableSlots(
            @RequestParam("parking_id") @NotNull(message = "Parking ID is required") Long parkingId,
            @RequestParam("vehicle_type") @NotNull(message = "Vehicle type is required") String vehicleType,
            @RequestParam(value = "start_time", required = false) String startTimeStr,
            @RequestParam(value = "end_time", required = false) String endTimeStr) {
        
        try {
            LocalDateTime startTime;
            LocalDateTime endTime;
            
            if (startTimeStr != null && endTimeStr != null) {
                // Parse provided times
                startTime = LocalDateTime.parse(startTimeStr);
                endTime = LocalDateTime.parse(endTimeStr);
                
                // Validate times in Nepal timezone
                LocalDateTime nowInNepal = LocalDateTime.now(NEPAL_TIMEZONE);
                
                if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
                    return ResponseEntity.badRequest().build();
                }
                
                if (startTime.isBefore(nowInNepal)) {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                // Default: next 24 hours from now in Nepal timezone
                startTime = LocalDateTime.now(NEPAL_TIMEZONE);
                endTime = startTime.plusDays(1);
            }
            
            List<Slot> slots = bookingService.getAvailableSlots(parkingId, vehicleType, startTime, endTime);
            return ResponseEntity.ok(slots);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}