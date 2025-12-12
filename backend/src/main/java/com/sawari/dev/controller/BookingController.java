package com.sawari.dev.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.dbtypes.UserRole;
import com.sawari.dev.model.Booking;
import com.sawari.dev.model.dto.BookingDetailsDTO;
import com.sawari.dev.model.dto.BookingRequest;
import com.sawari.dev.repository.BookingRepository;
import com.sawari.dev.repository.SlotRepository;
import com.sawari.dev.service.BookingDetailService;
import com.sawari.dev.service.CustomUserDetails;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BookingController {
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final BookingDetailService bookingService;

    @PostMapping("/bookSlot")
    public ResponseEntity<?> bookSlot(@RequestBody BookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();

        LocalDateTime endTime = request.getStartTime().plusMinutes(request.getDurationMinutes());

        Double basePrice = slotRepository.findBasePriceBySlotId(request.getSlotId());
        Double totalPrice = basePrice * (request.getDurationMinutes() / 60.0);

        Booking newBooking = new Booking();
        newBooking.setUserId(authenticatedUserId);
        newBooking.setSlotId(request.getSlotId());
        newBooking.setVehicleId(request.getVehicleId());
        newBooking.setExpectedStartingTime(request.getStartTime());
        newBooking.setExpectedEndTime(endTime);
        newBooking.setTotalAmount(totalPrice);
        newBooking.setBookingStatus(BookingStatus.RESERVED);

        bookingRepository.save(newBooking);
        slotRepository.updateReserveStatusOnBooked(request.getSlotId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy, hh:mm a");

        String formattedStart = newBooking.getExpectedStartingTime().format(formatter);
        String formattedEnd = newBooking.getExpectedEndTime().format(DateTimeFormatter.ofPattern("hh:mm a"));

        Map<String, Object> response = new HashMap<>();

        response.put("location", slotRepository.getParkingNameBySlotId(request.getSlotId()));
        response.put("slotNumber", request.getSlotId());
        response.put("time", formattedStart + " - " + formattedEnd);
        response.put("duration", request.getDurationMinutes());
        response.put("totalPrice", totalPrice);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getBookingDetails")
    public ResponseEntity<?> getBookingDetailsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();

        return ResponseEntity.ok(bookingService.findBookingsForUser(authenticatedUserId));
    }
}
