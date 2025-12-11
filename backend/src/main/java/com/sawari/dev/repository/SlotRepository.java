package com.sawari.dev.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>{
    List<Slot> findByParkingId(Long parkingId);
    
    // Count available slots for a parking
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isOccupied = false AND s.isReserved = false")
    int countAvailableSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count occupied slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isOccupied = true")
    int countOccupiedSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count reserved slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isReserved = true")
    int countReservedSlotsByParkingId(@Param("parkingId") Long parkingId);

    @Query("SELECT COUNT(s) FROM Slot s WHERE s.isOccupied = true OR s.isReserved = true")
    int countBookedSlots();
    
    List<Slot> findByParkingId(Long parkingId);
    
    // Find slots by parking and type that are not occupied or reserved
    List<Slot> findByParkingIdAndSlotTypeAndIsOccupiedFalseAndIsReservedFalse(
            Long parkingId, VehicleModel slotType);
    
    // Find slots by parking and slot type (JPA derived query)
    List<Slot> findByParkingIdAndSlotType(Long parkingId, VehicleModel slotType);
    
    // Find all reserved slots
    List<Slot> findByIsReservedTrue();
    
    // Find slots by ID list that are not reserved
    List<Slot> findBySlotIdInAndIsReservedFalse(List<Long> slotIds);
    
    // Count available slots for a parking
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isOccupied = false AND s.isReserved = false")
    int countAvailableSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count occupied slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isOccupied = true")
    int countOccupiedSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count reserved slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parkingId = :parkingId AND s.isReserved = true")
    int countReservedSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Time-based availability query - excludes slots with overlapping bookings
    @Query("SELECT s FROM Slot s " +
           "WHERE s.parkingId = :parkingId " +
           "AND s.slotType = :slotType " +
           "AND s.isOccupied = false " +
           "AND NOT EXISTS (" +
           "    SELECT b FROM Booking b " +
           "    WHERE b.slotId = s.slotId " +
           "    AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "    AND b.expectedStartingTime < :endTime " +
           "    AND b.expectedEndTime > :startTime" +
           ")")
    List<Slot> findAvailableSlotsForTimePeriod(
            @Param("parkingId") Long parkingId,
            @Param("slotType") VehicleModel slotType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Pessimistic write lock to prevent concurrent booking of same slot
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.slotId = :slotId")
    Optional<Slot> findByIdWithLock(@Param("slotId") Long slotId);
    
    // Original method for backward compatibility (without time constraints)
    @Query("SELECT s FROM Slot s " +
           "WHERE s.parkingId = :parkingId " +
           "AND s.slotType = :slotType " +
           "AND s.isReserved = false " +
           "AND s.isOccupied = false")
    List<Slot> findAvailableSlots(
            @Param("parkingId") Long parkingId,
            @Param("slotType") VehicleModel slotType);
}