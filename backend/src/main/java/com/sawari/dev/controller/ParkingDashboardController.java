package com.sawari.dev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.dbtypes.UserRole;
import com.sawari.dev.model.Parking;
import com.sawari.dev.model.Slot;
import com.sawari.dev.model.dto.ParkingSlotDTO;
import com.sawari.dev.model.dto.SlotDetailDTO;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.repository.SlotRepository;
import com.sawari.dev.service.CustomUserDetails;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingDashboardController {

    private final ParkingRepository parkingRepository;
    private final SlotRepository slotRepository;

    public ParkingDashboardController(ParkingRepository parkingRepository, SlotRepository slotRepository) {
        this.parkingRepository = parkingRepository;
        this.slotRepository = slotRepository;
    }

  
    @GetMapping("/parkings")
    public ResponseEntity<?> getAllParkings() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long authenticatedUserId = userDetails.getId();
            UserRole authenticatedUserRole = userDetails.getRole();
            
            List<Parking> parkings;
            
            // Role-based filtering
            if (authenticatedUserRole.toString().equals("PARKING_OWNER")) {
                // Parking owner sees only their parkings
                parkings = parkingRepository.findByOwnerId(authenticatedUserId);
            } else if (authenticatedUserRole.toString().equals("ADMIN") || 
                       authenticatedUserRole.toString().equals("CUSTOMER")) {
                // Admin and customers see all active parkings
                parkings = parkingRepository.findByIsActive(true);
            } else {
                response.put("status", "error");
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Convert to DTO with slot information
            List<ParkingSlotDTO> parkingDTOs = new ArrayList<>();
            
            for (Parking parking : parkings) {
                int totalSlots = parking.getTwoWheelerSpaceCount() + parking.getFourWheelerSpaceCount();
                int availableSlots = slotRepository.countAvailableSlotsByParkingId(parking.getParkingId());
                int occupiedSlots = slotRepository.countOccupiedSlotsByParkingId(parking.getParkingId());
                int reservedSlots = slotRepository.countReservedSlotsByParkingId(parking.getParkingId());
                
                ParkingSlotDTO dto = new ParkingSlotDTO(
                    parking.getParkingId(),
                    parking.getLocation(),
                    parking.getAddress(),
                    parking.getImageLink(),
                    parking.isActive(),
                    totalSlots,
                    availableSlots,
                    occupiedSlots,
                    reservedSlots
                );
                
                parkingDTOs.add(dto);
            }
            
            response.put("status", "success");
            response.put("role", authenticatedUserRole.toString());
            response.put("parkings", parkingDTOs);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fetching parkings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
  // yo chai parking id parking owner saga related cha 
    @GetMapping("/parkings/{parkingId}/slots")
    public ResponseEntity<?> getParkingSlots(@PathVariable Long parkingId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long authenticatedUserId = userDetails.getId();
            UserRole authenticatedUserRole = userDetails.getRole();
            
            // parking details lina 
            Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
            
            // Check if parking owner is requesting their own parking
            if (authenticatedUserRole.toString().equals("PARKING_OWNER") && 
                !parking.getOwnerId().equals(authenticatedUserId)) {
                response.put("status", "error");
                response.put("message", "You can only view your own parkings");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // sabai slotes get garna 
            List<Slot> slots = slotRepository.findByParkingId(parkingId);
            
            // Convert to DTO
           // In getParkingSlots() method
List<SlotDetailDTO> slotDTOs = new ArrayList<>();

for (Slot slot : slots) {
    String status;
    if (slot.getIsOccupied()) {
        status = "occupied";
    } else if (slot.getIsReserved()) {
        status = "reserved";
    } else {
        status = "available";
    }
    
    // Now the constructor parameters match the field types
    SlotDetailDTO dto = new SlotDetailDTO(
        slot.getSlotId(),    
        slot.getParkingId(), 
        slot.getSlotNumber(),
        slot.getSlotType(),
        slot.getIsOccupied(),  
        slot.getIsReserved(),  
        status                 
    );
    
    slotDTOs.add(dto);
}
            
            response.put("status", "success");
            response.put("parking", new ParkingSlotDTO(
                parking.getParkingId(),
                parking.getLocation(),
                parking.getAddress(),
                parking.getImageLink(),
                parking.isActive(),
                parking.getTwoWheelerSpaceCount() + parking.getFourWheelerSpaceCount(),
                slotRepository.countAvailableSlotsByParkingId(parkingId),
                slotRepository.countOccupiedSlotsByParkingId(parkingId),
                slotRepository.countReservedSlotsByParkingId(parkingId)
            ));
            response.put("slots", slotDTOs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fetching slots: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}