package com.sawari.dev.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
          List<Slot> findByParkingId(Long parkingId);
    
    // Find slots by parking and type that are not occupied or reserved
    List<Slot> findByParkingIdAndSlotTypeAndIsOccupiedFalseAndIsReservedFalse(
            Long parkingId, VehicleModel slotType);
    
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
    
    // Time-based availability query
    @Query("SELECT s FROM Slot s " +
           "WHERE s.parkingId = :parkingId " +
           "AND s.slotType = :slotType " +
           "AND s.isOccupied = false " +
           "AND NOT EXISTS (" +
           "    SELECT b FROM Booking b " +
           "    WHERE b.slotId = s.slotId " +
           "    AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
           "    AND (" +
           "        (b.expectedStartingTime < :endTime AND b.expectedEndTime > :startTime) " +
           "    )" +
           ")")
    List<Slot> findAvailableSlotsForTimePeriod(
            @Param("parkingId") Long parkingId,
            @Param("slotType") VehicleModel slotType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}