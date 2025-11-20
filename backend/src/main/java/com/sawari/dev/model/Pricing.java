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
@Table(name = "pricing")
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long price_id;

    @Column(nullable = false)
    private Long parking_id;
    private Double rate_per_hour;
    private Double overtime_rate_per_hour;
    private int grace_period_minutes;

    @Enumerated(EnumType.STRING)
    private VehicleModel vehicle_type;

    // Constructors
    public Pricing() {}

    public Pricing(Long parking_id, VehicleModel vehicle_type, Double rate_per_hour, Double overtime_rate_per_hour, int grace_period_minutes) {
        this.parking_id = parking_id;
        this.vehicle_type = vehicle_type;
        this.rate_per_hour = rate_per_hour;
        this.overtime_rate_per_hour = overtime_rate_per_hour;
        this.grace_period_minutes = grace_period_minutes;
    }

    // Getters and Setters
    public Long getPriceId() {
        return price_id;
    }

    public Long getParkingId() {
        return parking_id;
    }

    public void setParkingId(Long parking_id) {
        this.parking_id = parking_id;
    }

    public VehicleModel getVehicleType() {
        return vehicle_type;
    }

    public void setVehicleType(VehicleModel vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public Double getRatePerHour() {
        return rate_per_hour;
    }

    public void setRatePerHour(Double rate_per_hour) {
        this.rate_per_hour = rate_per_hour;
    }

    public Double getOvertimeRatePerHour() {
        return overtime_rate_per_hour;
    }

    public void setOvertimeRatePerHour(Double overtime_rate_per_hour) {
        this.overtime_rate_per_hour = overtime_rate_per_hour;
    }

    public int getGracePeriodMinutes() {
        return grace_period_minutes;
    }

    public void setGracePeriodMinutes(int grace_period_minutes) {
        this.grace_period_minutes = grace_period_minutes;
    }

}
