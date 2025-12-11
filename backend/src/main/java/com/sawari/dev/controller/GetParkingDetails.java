package com.sawari.dev.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Parking;
import com.sawari.dev.model.Slot;
import com.sawari.dev.repository.AnalysisRepository;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.repository.SlotRepository;
import com.sawari.dev.service.CustomUserDetails;

import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GetParkingDetails {

    private final ParkingRepository parkingRepository;
    private final SlotRepository slotRepository;
    private final AnalysisRepository analysisRepository;

    @GetMapping("/parkingInfo")
    public List<Parking> getAllParking() {
        return parkingRepository.findAll();
    }

    @GetMapping("/slotInfo")
    public ResponseEntity<?> findBookedSlots() {
        Map<String, Object> response = new HashMap<>();

        int bookedSlots = slotRepository.countBookedSlots();

        response.put("bookedSlots", bookedSlots);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/monthlySpending")
    public ResponseEntity<?> findMonthlySpending() {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();

        Double monthlySpending = analysisRepository.findMonthlyTotalByUserId(authenticatedUserId);

        response.put("monthlySpending", monthlySpending);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/parkingById/{id}")
    public ResponseEntity<?> findParkingById(@PathVariable Long id) {
        Parking parkingInfo = parkingRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Parking not found"));

        return ResponseEntity.ok(parkingInfo);
    }

    @GetMapping("/slotByParkingId/{id}")
    public List<Slot> getSlotDetails(@PathVariable Long id) {
        return slotRepository.findByParkingId(id);
    }
    
}
