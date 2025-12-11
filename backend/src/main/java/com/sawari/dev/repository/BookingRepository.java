package com.sawari.dev.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sawari.dev.dbtypes.BookingStatus;
import com.sawari.dev.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByUserIdAndBookingStatus(Long userId, BookingStatus status);
    
    // Find bookings that are active now or in the future for a specific slot
    @Query("SELECT b FROM Booking b " +
       "WHERE b.slotId = :slotId " +
       "AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
       "AND b.expectedEndTime > :now " +  // ✅ Keep bookings that haven't ended
       "ORDER BY b.expectedStartingTime ASC")
List<Booking> findUpcomingBookingsForSlot(
        @Param("slotId") Long slotId,
        @Param("now") LocalDateTime now);
    
    // 🔧 FIX: Properly check for overlapping bookings
    // Two time ranges overlap if: range1.start < range2.end AND range1.end > range2.start
    // A booking overlaps with our requested time if:
    // - The booking starts before our end time AND
    // - The booking ends after our start time
    @Query("SELECT b FROM Booking b " +
           "WHERE b.slotId = :slotId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.expectedStartingTime < :endTime " +
           "AND b.expectedEndTime > :startTime")
    List<Booking> findOverlappingBookingsForSlot(
            @Param("slotId") Long slotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    //  FIX: Added method to find conflicting bookings for multiple slots at once
    // This is more efficient for checking availability across multiple slots
    // Uses the same overlap logic as above
    @Query("SELECT b FROM Booking b " +
           "WHERE b.slotId IN :slotIds " +
           "AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.expectedStartingTime < :endTime " +
           "AND b.expectedEndTime > :startTime")
    List<Booking> findConflictingBookings(
            @Param("slotIds") List<Long> slotIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Find slot IDs with bookings starting before a threshold
    @Query("SELECT DISTINCT b.slotId FROM Booking b " +
           "WHERE b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.expectedStartingTime <= :threshold")
    List<Long> findSlotIdsWithBookingsStartingBefore(@Param("threshold") LocalDateTime threshold);
    
}