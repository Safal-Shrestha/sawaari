package com.sawari.dev.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;

@RestController
@RequestMapping("/api")
public class GetParkingController {

    private final ParkingRepository parkingRepository;

    public GetParkingController(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }


    @GetMapping("/parkingInfo")
    public List<Parking> getAllParking() {
        return parkingRepository.findAll();
    }
}
