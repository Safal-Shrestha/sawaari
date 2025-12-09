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
           "AND b.expectedEndTime > :now " +
           "AND b.expectedStartingTime > :now")
    List<Booking> findUpcomingBookingsForSlot(
            @Param("slotId") Long slotId,
            @Param("now") LocalDateTime now);
    
    // Find overlapping bookings for a specific slot
    @Query("SELECT b FROM Booking b " +
           "WHERE b.slotId = :slotId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND (" +
           "    (b.expectedStartingTime < :endTime AND b.expectedEndTime > :startTime) " +
           ")")
    List<Booking> findOverlappingBookingsForSlot(
            @Param("slotId") Long slotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Find slot IDs with bookings starting before a threshold
    @Query("SELECT DISTINCT b.slotId FROM Booking b " +
           "WHERE b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.expectedStartingTime <= :threshold")
    List<Long> findSlotIdsWithBookingsStartingBefore(@Param("threshold") LocalDateTime threshold);
    
}