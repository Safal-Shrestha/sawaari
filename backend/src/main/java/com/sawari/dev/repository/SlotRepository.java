package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>{
    
}
