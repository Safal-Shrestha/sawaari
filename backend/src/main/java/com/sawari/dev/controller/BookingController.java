package com.sawari.dev.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.dto.CreateBookingRequest;
import com.sawari.dev.model.dto.BookingResponse;
import com.sawari.dev.service.BookingService;
import com.sawari.dev.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

  
    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        
       
        Long userId = getCurrentUserId();
        
      
        BookingResponse response = bookingService.createBooking(userId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestParam(required = false) BookingStatus status) {
        
        Long userId = getCurrentUserId();
        
        List<BookingResponse> bookings = bookingService.getUserBookings(userId, status);
        
        return ResponseEntity.ok(bookings);
    }

   
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId) {
        
        Long userId = getCurrentUserId();
        
        BookingResponse booking = bookingService.getBookingById(bookingId, userId);
        
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<SlotDTO>> getAvailableSlots(
            @RequestParam Long parkingId,
            @RequestParam VehicleType vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
                LocalDateTime startTime,
            @RequestParam BigDecimal duration) {
        
        List<SlotDTO> availableSlots = bookingService.getAvailableSlots(
                parkingId, vehicleType, startTime, duration);
        
        return ResponseEntity.ok(availableSlots);
    }

    
    @GetMapping("/calculate-cost")
    public ResponseEntity<BigDecimal> calculateCost(
            @RequestParam Long parkingId,
            @RequestParam VehicleType vehicleType,
            @RequestParam BigDecimal duration) {
        
        BigDecimal cost = bookingService.calculateCost(parkingId, vehicleType, duration);
        
        return ResponseEntity.ok(cost);
    }

    
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        
        Long userId = getCurrentUserId();
        
        bookingService.cancelBooking(bookingId, userId);
        
        return ResponseEntity.ok("Booking cancelled successfully");
    }

   
    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long bookingId) {
        
        Long userId = getCurrentUserId();
        
        BookingResponse response = bookingService.checkIn(bookingId, userId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<BookingResponse> checkOut(@PathVariable Long bookingId) {
        
        Long userId = getCurrentUserId();
        
        BookingResponse response = bookingService.checkOut(bookingId, userId);
        
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userDetails.getId();
    }
}
