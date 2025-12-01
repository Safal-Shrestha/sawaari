package com.sawari.dev.controller.ParkingOwner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.service.CustomUserDetails;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OwnerParkingController { 

    private final ParkingRepository parkingRepository;

    public OwnerParkingController(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @GetMapping("/ownerParking")
public ResponseEntity<?> getOwnerParking(
        @RequestParam(required = false) Boolean active) {
    
    Map<String, Object> response = new HashMap<>();

    try {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("status", "error");
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long ownerId = userDetails.getId();

        // Fetch parking based on active 
        List<Parking> parkingList;

        if (active != null) {
            parkingList = parkingRepository.findByOwnerIdAndIsActive(ownerId, active);
        } else {
            parkingList = parkingRepository.findByOwnerId(ownerId);
        }

        response.put("status", "success");
        response.put("count", parkingList.size());
        response.put("parkingList", parkingList);

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("status", "error");
        response.put("message", "Something went wrong: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

    
}