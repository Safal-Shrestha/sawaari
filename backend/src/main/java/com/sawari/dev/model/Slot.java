package com.sawari.dev.model;

import com.sawari.dev.dbtypes.VehicleModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "slot")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slot_id;

    @Column(nullable = false)
    private Long parking_id;
    private Long slot_number;
    private Boolean is_occupied;
    private Boolean is_reserved;

    @Enumerated(EnumType.STRING)
    private VehicleModel slot_type;

    // Constructors
    public Slot() {}

    public Slot(Long parking_id, Long slot_number, VehicleModel slot_type, Boolean is_occupied, Boolean is_reserved) {
        this.parking_id = parking_id;
        this.slot_number = slot_number;
        this.slot_type = slot_type;
        this.is_occupied = is_occupied;
        this.is_reserved = is_reserved;
    }

    // Getters and Setters
    public Long getSlotId() {
        return slot_id;
    }

    public Long getParkingId() {
        return parking_id;
    }

    public void setParkingId(Long parking_id) {
        this.parking_id = parking_id;
    }

    public Long getSlotNumber() {
        return slot_number;
    }

    public void setSlotNumber(Long slot_number) {
        this.slot_number = slot_number;
    }

    public VehicleModel getSlotType() {
        return slot_type;
    }

    public void setSlotType(VehicleModel slot_type) {
        this.slot_type = slot_type;
    }

    public Boolean getIsOccupied() {
        return is_occupied;
    }

    public void setIsOccupied(Boolean is_occupied) {
        this.is_occupied = is_occupied;
    }

    public Boolean getIsReserved() {
        return is_reserved;
    }

    public void setIsReserved(Boolean is_reserved) {
        this.is_reserved = is_reserved;
    }
    
}
