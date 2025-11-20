package com.sawari.dev.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.dbtypes.VehicleModel;
import com.sawari.dev.model.Vehicle;
import com.sawari.dev.repository.VehicleRepository;



@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class AddNewVehicle {
    private final VehicleRepository vehicleRepository;

    public AddNewVehicle(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping("/addNewVehicle")
    public Vehicle addNewVehicle(
        @RequestParam("v_id") String vId,
        @RequestParam("v_model") VehicleModel vModel,
        @RequestParam("user_id") Long uId
    ) {

        Vehicle vehicle = new Vehicle();

        vehicle.setUserId(uId);
        vehicle.setVehicleModel(vModel);
        vehicle.setVehicleId(vId);

        return vehicleRepository.save(vehicle);
    }
    
}
