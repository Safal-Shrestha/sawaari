package com.sawari.dev.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sawari.dev.model.Slot;

import jakarta.transaction.Transactional;

public interface SlotRepository extends JpaRepository<Slot, Long>{
    List<Slot> findByParking_ParkingId(Long parkingId);
    
    // Count available slots for a parking
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parking.parkingId = :parkingId AND s.isOccupied = false AND s.isReserved = false")
    int countAvailableSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count occupied slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parking.parkingId = :parkingId AND s.isOccupied = true")
    int countOccupiedSlotsByParkingId(@Param("parkingId") Long parkingId);
    
    // Count reserved slots
    @Query("SELECT COUNT(s) FROM Slot s WHERE s.parking.parkingId = :parkingId AND s.isReserved = true")
    int countReservedSlotsByParkingId(@Param("parkingId") Long parkingId);

    @Query("SELECT COUNT(s) FROM Slot s WHERE s.isOccupied = true OR s.isReserved = true")
    int countBookedSlots();    
    
    // Find all reserved slots
    List<Slot> findByIsReservedTrue();
    
    // Find slots by ID list that are not reserved
    List<Slot> findBySlotIdInAndIsReservedFalse(List<Long> slotIds);

    @Query("SELECT CASE " +
       "WHEN s.slotType = 'TWO_WHEELER' THEN p.twoWheelerRatePerHour " +
       "WHEN s.slotType = 'FOUR_WHEELER' THEN p.fourWheelerRatePerHour " +
       "END " +
       "FROM Slot s JOIN s.parking p " +
       "WHERE s.slotId = :slotId")
    Double findBasePriceBySlotId(@Param("slotId") Long slotId);  
       
    @Query("SELECT p.location FROM Slot s JOIN s.parking p WHERE s.slotId = :slotId")
    String getParkingNameBySlotId(@Param("slotId") Long slotId);
    
    @Transactional
    @Modifying
    @Query("UPDATE Slot s SET s.isReserved = true WHERE s.slotId = :slotId")
    int updateReserveStatusOnBooked(@Param("slotId") Long slotId);
}