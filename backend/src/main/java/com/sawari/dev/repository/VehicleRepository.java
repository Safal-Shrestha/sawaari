package com.sawari.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String>{
    public List<Vehicle> findAllVehicleByuserId(Long id);
}
