package com.sawari.dev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    
    private String v_id;

    @Column(nullable = false)
    private String v_model;
    private Long user_id;

    // Constructors
    public Vehicle() {}

    public Vehicle(String v_model, Long user_id) {
        this.v_model = v_model;
        this.user_id = user_id;
    }

    // Getters and Setter
    public String getVehicleId() {
        return v_id;
    }

    public String getVehicleModel() {
        return v_model;
    }

    public void setVehicleModel(String v_model) {
        this.v_model = v_model;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }
}
