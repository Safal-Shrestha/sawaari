package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Parking;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
