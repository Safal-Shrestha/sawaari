package com.sawari.dev.controller.ParkingOwner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sawari.dev.dbtypes.UserRole;
import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.Parking;
import com.sawari.dev.model.Slot;
import com.sawari.dev.repository.ParkingRepository;
import com.sawari.dev.repository.SlotRepository;
import com.sawari.dev.service.CustomUserDetails;

@RestController
@RequestMapping("/api")
public class AddNewParking {

    private final ParkingRepository parkingRepository;
    private final SlotRepository slotRepository;

    public AddNewParking(ParkingRepository parkingRepository, SlotRepository slotRepository) {
        this.parkingRepository = parkingRepository;
        this.slotRepository = slotRepository;
    }

    @PostMapping(value = "/addNewParking",
    consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> addNewParking(
            @RequestParam("location") String plocation,
            @RequestParam("address") String paddress,
            @RequestParam("two_wheeler_space_count") int twoWheelerSpaceCount,
            @RequestParam("four_wheeler_space_count") int fourWheelerSpaceCount,
            @RequestParam("is_active") boolean isActive,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("bike_rate") String bikeRate,
            @RequestParam("car_rate") String carRate,
            @RequestParam("image") MultipartFile parkingImage
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Extract user details from JWT token
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long authenticatedUserId = userDetails.getId();
            UserRole authenticatedUserRole = userDetails.getRole();
        
            if (!authenticatedUserRole.toString().equals("PARKING_OWNER") && !authenticatedUserRole.toString().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (parkingImage.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Image is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String imageName = UUID.randomUUID() + "_" + parkingImage.getOriginalFilename();
            String uploadDir = new File("backend/src/main/resources/static/parking_lot_images").getAbsolutePath();

            File destinationFile = new File(uploadDir + File.separator + imageName);
            parkingImage.transferTo(destinationFile);
          
            Parking parking = new Parking();
            parking.setActive(isActive);
            parking.setAddress(paddress);
            parking.setFourWheelerSpaceCount(fourWheelerSpaceCount);
            parking.setTwoWheelerSpaceCount(twoWheelerSpaceCount);
            parking.setLocation(plocation);
            parking.setOwnerId(authenticatedUserId);
            parking.setImageLink("http://localhost:8080/parking_lot_images/" + imageName);
            BigDecimal lat = new BigDecimal(latitude);
            BigDecimal lon = new BigDecimal(longitude);
            parking.setLatitude(lat);
            parking.setLongitude(lon);
            Double twoWheelerRate = Double.valueOf(bikeRate);
            Double fourWheelerRate = Double.valueOf(carRate);
            parking.setTwoWheelerRatePerHour(twoWheelerRate);
            parking.setFourWheelerRatePerHour(fourWheelerRate);

            Parking savedParking;
            savedParking = parkingRepository.save(parking);

            // slots create garna 
            for (int i = 1; i <= twoWheelerSpaceCount; i++) {
                Slot slot = new Slot();
                slot.setParkingId(savedParking.getParkingId());
                slot.setSlotNumber((long) i);
                slot.setSlotType(VehicleModel.TWO_WHEELER);
                slot.setIsOccupied(false);
                slot.setIsReserved(false);
                slotRepository.save(slot);
            }

            // Create four-wheeler slots (continue numbering)
            for (int i = 1; i <= fourWheelerSpaceCount; i++) {
                Slot slot = new Slot();
                slot.setParkingId(savedParking.getParkingId());
                slot.setSlotNumber((long) (twoWheelerSpaceCount + i));
                slot.setSlotType(VehicleModel.FOUR_WHEELER);
                slot.setIsOccupied(false);
                slot.setIsReserved(false);
                slotRepository.save(slot);
            }

            response.put("status", "success");
            response.put("message", "Parking location and " + (twoWheelerSpaceCount + fourWheelerSpaceCount) + " slots created successfully!");
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