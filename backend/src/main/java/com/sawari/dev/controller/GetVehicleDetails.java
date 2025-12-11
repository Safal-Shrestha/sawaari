package com.sawari.dev.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Vehicle;
import com.sawari.dev.repository.VehicleRepository;
import com.sawari.dev.service.CustomUserDetails;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GetVehicleDetails {
    private final VehicleRepository vehicleRepository;


    @GetMapping("/vehicleById")
    public List<Vehicle> getVehicleById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();



        System.out.println(vehicleRepository.findAllVehicleByuserId(authenticatedUserId));
        return vehicleRepository.findAllVehicleByuserId(authenticatedUserId);
    }
}
