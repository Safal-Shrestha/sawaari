package com.sawari.dev.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.service.CustomUserDetails;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AddNewParking {

    private final ParkingRepository parkingRepository;

    public AddNewParking(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @PostMapping("/addNewParking")
    public ResponseEntity<?> addNewParking(
            @RequestParam("owner_id") Long ownerId,
            @RequestParam("location") String plocation,
            @RequestParam("address") String paddress,
            @RequestParam("two_wheeler_space_count") int twoWheelerSpaceCount,
            @RequestParam("four_wheeler_space_count") int fourWheelerSpaceCount,
            @RequestParam("is_active") boolean isActive,
            @RequestParam("image") MultipartFile parkingImage
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("status", "error");
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract user details from JWT token
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long authenticatedUserId = userDetails.getId();

            // Verify that the owner_id matches the authenticated user
            if (!authenticatedUserId.equals(ownerId)) {
                response.put("status", "error");
                response.put("message", "Unauthorized: You can only add parking for yourself");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (parkingImage.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Image is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String imageName = UUID.randomUUID() + "_" + parkingImage.getOriginalFilename();
            String uploadDir = new File("src/main/resources/static/parking_lot_images").getAbsolutePath();

            File destinationFile = new File(uploadDir + File.separator + imageName);
            parkingImage.transferTo(destinationFile);

            Parking parking = new Parking();
            parking.setActive(isActive);
            parking.setAddress(paddress);
            parking.setFourWheelerSpaceCount(fourWheelerSpaceCount);
            parking.setTwoWheelerSpaceCount(twoWheelerSpaceCount);
            parking.setLocation(plocation);
            parking.setOwnerId(ownerId);
            parking.setImageLink("http://localhost:8080/parking_lot_images/" + imageName);

            parkingRepository.save(parking);

            response.put("status", "success");
            response.put("message", "Parking location saved successfully!");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Error while saving image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}