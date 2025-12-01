package com.sawari.dev.controller;

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
    public ResponseEntity<?> getOwnerParking() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get authenticated user from JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract owner ID from JWT token (logged-in parking owner)
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long ownerId = userDetails.getId();

            // Fetch all parkings owned by this parking owner
            List<Parking> parkingList = parkingRepository.findByOwnerId(ownerId);

            if (parkingList.isEmpty()) {
                response.put("status", "success");
                response.put("message", "No parking locations found for this owner.");
                response.put("parkingList", parkingList);
            } else {
                response.put("status", "success");
                response.put("message", "Parkings retrieved successfully");
                response.put("count", parkingList.size());
                response.put("parkingList", parkingList);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/ownerActiveParking")
    public ResponseEntity<?> getOwnerActiveParking() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get authenticated user from JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract owner ID from JWT token (logged-in parking owner)
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long ownerId = userDetails.getId();

            // Fetch only active parkings owned by this parking owner
            List<Parking> activeParkingList = parkingRepository.findByOwnerIdAndIsActive(ownerId, true);

            if (activeParkingList.isEmpty()) {
                response.put("status", "success");
                response.put("message", "No active parking locations found for this owner.");
                response.put("parkingList", activeParkingList);
            } else {
                response.put("status", "success");
                response.put("message", "Active parkings retrieved successfully");
                response.put("count", activeParkingList.size());
                response.put("parkingList", activeParkingList);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}