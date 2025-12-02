package com.sawari.dev.repository;

import com.sawari.dev.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by user ID
    List<Booking> findByUser_id(Long userId);
    
    // Convenience method matching camelCase naming used in service
    @Query("SELECT b FROM Booking b WHERE b.user_id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);
    
    // Find bookings by vehicle ID
    List<Booking> findByVehicleid(String vehicleId);

    @Query("SELECT b FROM Booking b WHERE b.vehicle_id = :vehicleId")
    List<Booking> findByVehicleId(@Param("vehicleId") String vehicleId);
    
    // Find bookings by parking ID
    List<Booking> findByParkingid(Long parkingId);

    @Query("SELECT b FROM Booking b WHERE b.parking_id = :parkingId")
    List<Booking> findByParkingId(@Param("parkingId") Long parkingId);
    
    // Find bookings by slot ID
    List<Booking> findBySlotid(Long slotId);

    @Query("SELECT b FROM Booking b WHERE b.slot_id = :slotId")
    List<Booking> findBySlotId(@Param("slotId") Long slotId);
    
    // Find bookings by booking status
    List<Booking> findByBookingstatus(String bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.booking_status = :bookingStatus")
    List<Booking> findByBookingStatus(@Param("bookingStatus") String bookingStatus);
    
    // Check for conflicting bookings (prevent double-booking)
    @Query("SELECT b FROM Booking b WHERE b.slot_id = :slotId " +
           "AND b.booking_status IN ('CONFIRMED', 'PENDING', 'ACTIVE') " +
           "AND b.expected_starting_time < :endTime " +
           "AND b.expected_end_time > :startTime")
    List<Booking> findConflictingBookings(
        @Param("slotId") Long slotId,
        @Param("startTime") Timestamp startTime,
        @Param("endTime") Timestamp endTime
    );
}// user id , parking id 