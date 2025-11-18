package com.sawari.dev.controller;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Parking;
import com.sawari.dev.repository.ParkingRepository;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class GetParkingDetails {

    private final ParkingRepository parkingRepository;

    public GetParkingDetails(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }


    @GetMapping("/parkingInfo")
    public List<Parking> getAllParking() {
        return parkingRepository.findAll();
    }
}
