package com.sawari.dev.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.model.Booking;
import com.sawari.dev.model.Slot;
import com.sawari.dev.model.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find all bookings for a specific user
    List<Booking> findByUserId(Long userId);
    
    // Find bookings by vehicle ID
    List<Booking> findByVehicleId(String vehicleId);
    
    // Find bookings by status (PENDING, CONFIRMED, etc.)
    List<Booking> findByBookingStatus(String status);
    
    // Find user's bookings sorted by date (newest first)
    List<Booking> findByUserIdOrderByBookingDateTimeDesc(Long userId);
    
    // Find user's bookings with specific status
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    // Find a specific booking for a user (for security - user can only access their own)
    Optional<Booking> findByBookingIdAndUserId(Long bookingId, Long userId);

    @Query("SELECT b FROM Booking b WHERE b.slotId = :slotId " +
           "AND b.bookingStatus != com.sawari.dev.dbtypes.BookingStatus.CANCELLED " +
           "AND ((b.expectedStartingTime < :endTime AND b.expectedEndTime > :startTime))")
    List<Booking> findConflictingBookings(
        @Param("slotId") String slotId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT s FROM Slot s " +
           "WHERE s.parkingId = :parkingId " +
           "AND s.vehicleType = :vehicleType " +
           "AND NOT EXISTS (" +
           "  SELECT b FROM Booking b " +
           "  WHERE b.slotId = s.slotId " +
           "  AND b.bookingStatus IN (com.sawari.dev.dbtypes.BookingStatus.CONFIRMED, " +
           "                          com.sawari.dev.dbtypes.BookingStatus.ACTIVE) " +
           "  AND b.expectedStartingTime < :endTime " +
           "  AND b.expectedEndTime > :startTime" +
           ")")
    List<Slot> findAvailableSlots(
        @Param("parkingId") Long parkingId,
        @Param("vehicleType") long vehicleType,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
        List<BookingResponse> getUserBookings(Long userId, BookingStatus status);

}