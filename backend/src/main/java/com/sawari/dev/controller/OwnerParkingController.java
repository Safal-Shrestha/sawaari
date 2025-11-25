package com.sawari.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;

@RestController
@CrossOrigin(origins = "*")
public class OwnerParkingController {

    private final ParkingRepository parkingRepository;

    public OwnerParkingController(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @GetMapping("/api/ownerParking")
    public ResponseEntity<?> getOwnerParking(@RequestParam("owner_id") Long ownerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Parking> parkingList = parkingRepository.findByOwnerId(ownerId);

            if (parkingList.isEmpty()) {
                response.put("status", "error");
                response.put("message", "No parking locations found for this owner.");
            } else {
                response.put("status", "success");
                response.put("parkingList", parkingList);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
