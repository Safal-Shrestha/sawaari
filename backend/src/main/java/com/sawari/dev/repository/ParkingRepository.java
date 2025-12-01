package com.sawari.dev.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {// yo parking repo class use garera curd operation garna milcha bhane 
    // <parking = entiry ho and long=pk ko datatype lai represent garcha 

     List<Parking> findByOwnerId(Long ownerId);
    
    List<Parking> findByOwnerIdAndIsActive(Long ownerId, boolean isActive);
}
