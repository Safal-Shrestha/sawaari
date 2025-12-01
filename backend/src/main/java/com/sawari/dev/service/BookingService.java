package com.sawari.dev.service;

import com.sawari.dev.model.Booking;
import com.sawari.dev.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // Create a new booking
    public Booking createBooking(Booking booking) {
        // 1. VALIDATE required fields
        if (booking.getUser_id() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (booking.getParking_id() == null) {
            throw new IllegalArgumentException("Parking ID is required");
        }
        if (booking.getSlot_id() == null) {
            throw new IllegalArgumentException("Slot ID is required");
        }
        if (booking.getVehicle_id() == null || booking.getVehicle_id().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (booking.getExpected_starting_time() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (booking.getExpected_end_time() == null) {
            throw new IllegalArgumentException("End time is required");
        }
        
        // 2. VALIDATE time logic
        if (booking.getExpected_end_time().before(booking.getExpected_starting_time())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        // 3. CHECK for conflicts (prevent double booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            booking.getSlot_id(),
            booking.getExpected_starting_time(),
            booking.getExpected_end_time()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Slot is already booked for this time period");
        }
        
        // 4. SET default values
        if (booking.getBooking_date_time() == null) {
            booking.setBooking_date_time(Timestamp.from(Instant.now()));
        }
        
        if (booking.getBooking_status() == null || booking.getBooking_status().isEmpty()) {
            booking.setBooking_status("PENDING");
        }
        
        if (booking.getFine_amount() == null) {
            booking.setFine_amount(BigDecimal.ZERO);
        }
        
        // 5. CALCULATE total if not provided
        if (booking.getTotal_amount() == null) {
            booking.setTotal_amount(
                booking.getBase_price().add(booking.getFine_amount())
            );
        }
        
        // 6. SAVE to database
        return bookingRepository.save(booking);
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
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
            if (bookingDetails.getUser_id() != null) {
                booking.setUser_id(bookingDetails.getUser_id());
            }
            if (bookingDetails.getParking_id() != null) {
                booking.setParking_id(bookingDetails.getParking_id());
            }
            if (bookingDetails.getSlot_id() != null) {
                booking.setSlot_id(bookingDetails.getSlot_id());
            }
            if (bookingDetails.getVehicle_id() != null) {
                booking.setVehicle_id(bookingDetails.getVehicle_id());
            }
            if (bookingDetails.getExpected_starting_time() != null) {
                booking.setExpected_starting_time(bookingDetails.getExpected_starting_time());
            }
            if (bookingDetails.getExpected_end_time() != null) {
                booking.setExpected_end_time(bookingDetails.getExpected_end_time());
            }
            if (bookingDetails.getBase_price() != null) {
                booking.setBase_price(bookingDetails.getBase_price());
            }
            if (bookingDetails.getFine_amount() != null) {
                booking.setFine_amount(bookingDetails.getFine_amount());
            }
            if (bookingDetails.getTotal_amount() != null) {
                booking.setTotal_amount(bookingDetails.getTotal_amount());
            }
            if (bookingDetails.getBooking_status() != null) {
                booking.setBooking_status(bookingDetails.getBooking_status());
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
    public List<Booking> getBookingsByUserId(Long user_Id) {
        return bookingRepository.findByUserId( user_Id);
    }

    // Get bookings by vehicle ID
    public List<Booking> getBookingsByVehicleId(String vehicle_Id) {
        return bookingRepository.findByVehicleId(vehicle_Id);
    }

    // Get bookings by status
    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByBookingStatus(status);
    }
}