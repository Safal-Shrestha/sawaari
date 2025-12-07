package com.sawari.dev.service;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.model.Booking;
import com.sawari.dev.model.dto.BookingResponse;
import com.sawari.dev.model.dto.CreateBookingRequest;
import com.sawari.dev.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    
    @Transactional
    public BookingResponse createBooking(Long userId, CreateBookingRequest request) {
        
        // Validate time
        if (request.getExpectedEndTime().isBefore(request.getExpectedStartingTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        if (request.getExpectedStartingTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the past");
        }
        
        // Check slot availability
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            request.getSlotId().toString(),
            request.getExpectedStartingTime(),
            request.getExpectedEndTime()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Slot already booked");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setParkingId(request.getParkingId());
        booking.setSlotId(request.getSlotId().toString());
        booking.setVehicleId(request.getVehicleId());
        booking.setExpectedStartingTime(request.getExpectedStartingTime());
        booking.setExpectedEndTime(request.getExpectedEndTime());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setFineAmount(BigDecimal.ZERO);
        
        // Calculate price
        long hours = Duration.between(
            request.getExpectedStartingTime(), 
            request.getExpectedEndTime()
        ).toHours();
        if (hours < 1) hours = 1;
        
        BigDecimal basePrice = BigDecimal.valueOf(50.00).multiply(BigDecimal.valueOf(hours));
        booking.setBasePrice(basePrice);
        booking.setTotalAmount(basePrice);
        
        // Save
        Booking saved = bookingRepository.save(booking);
        
        // Return response
        return BookingResponse.builder()
                .bookingId(saved.getBookingId())
                .userId(saved.getUserId())
                .parkingId(saved.getParkingId())
                .slotId(Long.valueOf(saved.getSlotId()))
                .vehicleId(saved.getVehicleId())
                .bookingDateTime(saved.getBookingDateTime())
                .expectedStartingTime(saved.getExpectedStartingTime())
                .expectedEndTime(saved.getExpectedEndTime())
                .bookingStatus(saved.getBookingStatus().name())
                .basePrice(saved.getBasePrice())
                .totalAmount(saved.getTotalAmount())
            .fineAmount(saved.getFineAmount())
            .build();
}

// Get booking by ID
public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Update booking
    public Booking updateBooking(Long id, Booking bookingDetails) {
    Optional<Booking> bookingOptional = bookingRepository.findById(id);
    
    if (bookingOptional.isPresent()) {
        Booking booking = bookingOptional.get();
        
        // Update ALL relevant fields (match your entity field names)
        if (bookingDetails.getUserId() != null) {
            booking.setUserId(bookingDetails.getUserId());
        }
        if (bookingDetails.getParkingId() != null) {
            booking.setParkingId(bookingDetails.getParkingId());
        }
        if (bookingDetails.getSlotId() != null) {
            booking.setSlotId(bookingDetails.getSlotId());
        }
        if (bookingDetails.getVehicleId() != null) {
            booking.setVehicleId(bookingDetails.getVehicleId());
        }
        if (bookingDetails.getExpectedStartingTime() != null) {
            booking.setExpectedStartingTime(bookingDetails.getExpectedStartingTime());
        }
        if (bookingDetails.getExpectedEndTime() != null) {
            booking.setExpectedEndTime(bookingDetails.getExpectedEndTime());
        }
        if (bookingDetails.getBasePrice() != null) {
            booking.setBasePrice(bookingDetails.getBasePrice());
        }
        if (bookingDetails.getFineAmount() != null) {
            booking.setFineAmount(bookingDetails.getFineAmount());
        }
        if (bookingDetails.getTotalAmount() != null) {
            booking.setTotalAmount(bookingDetails.getTotalAmount());
        }
        if (bookingDetails.getBookingStatus() != null) {
            booking.setBookingStatus(bookingDetails.getBookingStatus());
        }
        
        return bookingRepository.save(booking);
    } else {
        throw new RuntimeException("Booking not found with id: " + id);
    }
}

   public void cancelBooking(Long bookingId, Long userId) {
    // Find the booking
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
    
    // Verify the booking belongs to the user (SECURITY CHECK)
    if (!booking.getUserId().equals(userId)) {
        throw new RuntimeException("You can only cancel your own bookings");
    }
    
    // Check if booking can be cancelled
    if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
        throw new RuntimeException("Cannot cancel a completed booking");
    }
    
    if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
        throw new RuntimeException("Booking is already cancelled");
    }
    
    // Update status to CANCELLED (don't delete it)
    booking.setBookingStatus(BookingStatus.CANCELLED);
    bookingRepository.save(booking);
}
    // Get bookings by user ID
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId( userId);
    }

    // Get bookings by vehicle ID
    public List<Booking> getBookingsByVehicleId(String vehicleId) {
        return bookingRepository.findByVehicleId(vehicleId);
    }

    // Get bookings by status
    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByBookingStatus(status);
    }
    
@Transactional
public BookingResponse checkIn(Long bookingId, Long userId) {
    
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    
    if (!booking.getUserId().equals(userId)) {
        throw new RuntimeException("Not your booking");
    }
    
    if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
        throw new RuntimeException("Can't check-in. Status: " + booking.getBookingStatus());
    }
    
    booking.setCheckInTime(LocalDateTime.now());
    booking.setBookingStatus(BookingStatus.ACTIVE);
    
    return convertToResponse(bookingRepository.save(booking));
}

@Transactional
public BookingResponse checkOut(Long bookingId, Long userId) {
    
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    
    if (!booking.getUserId().equals(userId)) {
        throw new RuntimeException("Not your booking");
    }
    
    if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
        throw new RuntimeException("Can't check-out. Status: " + booking.getBookingStatus());
    }
    
    LocalDateTime now = LocalDateTime.now();
    booking.setCheckOutTime(now);
    booking.setBookingStatus(BookingStatus.COMPLETED);
    
    // Calculate late fee if overstayed
    if (now.isAfter(booking.getExpectedEndTime())) {
        long lateHours = Duration.between(booking.getExpectedEndTime(), now).toHours() + 1;
        BigDecimal lateFee = BigDecimal.valueOf(25.00 * lateHours);
        booking.setFineAmount(lateFee);
        booking.setTotalAmount(booking.getBasePrice().add(lateFee));
    }
    
    return convertToResponse(bookingRepository.save(booking));
}

private BookingResponse convertToResponse(Booking booking) {
    return BookingResponse.builder()
            .bookingId(booking.getBookingId())
            .userId(booking.getUserId())
            .parkingId(booking.getParkingId())
            .slotId(Long.valueOf(booking.getSlotId()))
            .vehicleId(booking.getVehicleId())
            .bookingDateTime(booking.getBookingDateTime())
            .expectedStartingTime(booking.getExpectedStartingTime())
            .expectedEndTime(booking.getExpectedEndTime())
            .bookingStatus(booking.getBookingStatus().name())
            .basePrice(booking.getBasePrice())
            .totalAmount(booking.getTotalAmount())
            .fineAmount(booking.getFineAmount())
            .build();
}
 public BigDecimal calculateCost(Long parkingId, long vehicleType, BigDecimal duration) {
       return duration.multiply(BigDecimal.valueOf(10));//for now  multiplying by 10
    }
   

@Transactional(readOnly = true)
public List<BookingResponse> getUserBookings(Long userId, BookingStatus status) {
    List<Booking> bookings = bookingRepository.findByUserIdAndStatus(userId, status);
    return bookings.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public List<Booking> getAvailableSlots(Long parkingId, long slotId, LocalDateTime startTime, BigDecimal duration) {
    // Calculate end time based on duration (assuming duration is in minutes)
    LocalDateTime endTime = startTime.plusMinutes(duration.longValue());
    
    // Find conflicting bookings for the slot
    List<Booking> conflicts = bookingRepository.findConflictingBookings(
            slotId,
            Timestamp.valueOf(startTime),
            Timestamp.valueOf(endTime)
    );
    
    // If no conflicts, the slot is available
    if (conflicts.isEmpty()) {
        return List.of(); // Return empty list indicating availability
    }
    
    return conflicts;
}

}