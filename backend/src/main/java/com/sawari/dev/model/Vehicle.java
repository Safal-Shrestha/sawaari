package com.sawari.dev.model;

import com.sawari.dev.dbtypes.VehicleModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    private String v_id;

    @Column(nullable = false)
    private Long user_id;

    @Enumerated(EnumType.STRING)
    private VehicleModel v_model;

    // Constructors
    public Vehicle() {}

    public Vehicle(VehicleModel v_model, Long user_id) {
        this.v_model = v_model;
        this.user_id = user_id;
    }

    // Getters and Setter
    public String getVehicleId() {
        return v_id;
    }

    public void setVehicleId(String v_id) {
        this.v_id = v_id;
    }

    public VehicleModel getVehicleModel() {
        return v_model;
    }

    public void setVehicleModel(VehicleModel v_model) {
        this.v_model = v_model;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }
}
