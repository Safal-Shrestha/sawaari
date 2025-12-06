package com.sawari.dev.service;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.model.Booking;
import com.sawari.dev.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // Create a new booking
   public Booking createBooking(Booking booking) {
    // 1. VALIDATE required fields
    if (booking.getUserId() == null) {
        throw new IllegalArgumentException("User ID is required");
    }
    if (booking.getParkingId() == null) {
        throw new IllegalArgumentException("Parking ID is required");
    }
    if (booking.getSlotId() == null) {
        throw new IllegalArgumentException("Slot ID is required");
    }
   if (booking.getVehicleId() == null) {
    throw new IllegalArgumentException("Vehicle ID is required");
}
    if (booking.getExpectedStartingTime() == null) {
        throw new IllegalArgumentException("Start time is required");
    }
    if (booking.getExpectedEndTime() == null) {
        throw new IllegalArgumentException("End time is required");
    }
    
    // 2. VALIDATE time logic
    if (booking.getExpectedEndTime().isBefore(booking.getExpectedStartingTime())) {
        throw new IllegalArgumentException("End time must be after start time");
    }
    
    // 3. CHECK for conflicts (prevent double booking)
    List<Booking> conflicts = bookingRepository.findConflictingBookings(
        
        booking.getSlotId(),
        booking.getExpectedStartingTime(),
        booking.getExpectedEndTime()
        
    );
    
    if (!conflicts.isEmpty()) {
        throw new RuntimeException("Slot is already booked for this time period");
    }
    
    // 4. SET default values
    if (booking.getBookingDateTime() == null) {
        booking.setBookingDateTime(LocalDateTime.now());
    }
    
if (booking.getBookingStatus() == null) {
    booking.setBookingStatus(BookingStatus.PENDING);  
}
    
    if (booking.getFineAmount() == null) {
        booking.setFineAmount(BigDecimal.ZERO);
    }
    
    // 5. CALCULATE total if not provided
    if (booking.getTotalAmount() == null) {
        booking.setTotalAmount(
            booking.getBasePrice().add(booking.getFineAmount())
        );
    }
    
    // 6. SAVE to database
    return bookingRepository.save(booking);
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

    // Delete booking
    public void deleteBooking(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
        } else {
            throw new RuntimeException("Booking not found with id: " + id);
        }
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
}