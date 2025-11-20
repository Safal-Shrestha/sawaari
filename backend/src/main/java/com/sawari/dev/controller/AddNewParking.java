package com.sawari.dev.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class AddNewParking {

    private final ParkingRepository parkingRepository;

    public AddNewParking(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }
    

    @PostMapping("/addNewParking")
    public Parking addNewParking(
        @RequestParam("owner_id") Long ownerId,
        @RequestParam("location") String plocation,
        @RequestParam("address") String paddress,
        @RequestParam("two_wheeler_space_count") int twoWheelerSpaceCount,
        @RequestParam("is_active") boolean isActive,
        @RequestParam("four_wheeler_space_count") int fourWheelerSpaceCount,
        @RequestParam("image") MultipartFile parkingImage
    ) throws IOException {
        if(parkingImage.isEmpty()) {
            throw new RuntimeException("Image is empty");
        }

        String imageName = UUID.randomUUID() + "_" + parkingImage.getOriginalFilename();
        String uploadDir = new File("src/main/resources/static/parking_lot_images").getAbsolutePath();
        File destinationFile = new File(uploadDir + File.separator + imageName);
        parkingImage.transferTo(destinationFile);

        Parking parking = new Parking();
        parking.setActive(isActive);
        parking.setAddress(paddress);
        parking.setFourWheelerSpaceCount(fourWheelerSpaceCount);
        parking.setImageLink("http://localhost:8080/parking_lot_images/" + imageName);
        parking.setTwoWheelerSpaceCount(twoWheelerSpaceCount);
        parking.setLocation(plocation);
        parking.setOwnerId(ownerId);

        return parkingRepository.save(parking);
    }
}
