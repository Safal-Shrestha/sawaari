package com.sawari.dev.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
}
