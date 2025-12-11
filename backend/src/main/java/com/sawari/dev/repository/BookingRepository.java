package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sawari.dev.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {    
}