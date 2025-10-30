package com.sawari.dev.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "analysis")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysis_id;

    @Column(nullable = false)
    private Long booking_id;
    private Long user_id;
    private Long parking_id;
    private Long slot_id;
    private String vehicle_id;
    private Timestamp entry_time;
    private Timestamp exit_time;
    private String fine_reason;
    private int  duration_minutes;
    private Double base_price;
    private Double fine_amount;
    private Double total_amount;
    private Timestamp created_at;

    // Constructors
    public Analysis() {}

    public Analysis(Long booking_id, Long user_id, Long parking_id, Long slot_id, String vehicle_id, Timestamp entry_time, Timestamp exit_time, String fine_reason, int duration_minutes, Double base_price, Double fine_amount, Double total_amount, Timestamp created_at)
    {
        this.booking_id = booking_id;
        this.user_id = user_id;
        this.parking_id = parking_id;
        this.slot_id = slot_id;
        this.vehicle_id = vehicle_id;
        this.entry_time = entry_time;
        this. exit_time = exit_time;
        this.fine_reason = fine_reason;
        this.duration_minutes = duration_minutes;
        this.base_price = base_price;
        this.fine_amount = fine_amount;
        this.total_amount = total_amount;
        this.created_at = created_at;
    }

    // Getters and Setters

    public Long getAnalysisId() {
        return analysis_id;
    }

    public Long getBookingId() {
        return booking_id;
    }

    public void setBookingId(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Long getParkingId() {
        return parking_id;
    }

    public void setParkingId(Long parking_id) {
        this.parking_id = parking_id;
    }

    public Long getSlotId() {
        return slot_id;
    }

    public void setSlotId(Long slot_id) {
        this.slot_id = slot_id;
    }

    public String getVehicleId() {
        return vehicle_id;
    }

    public void setVehicleId(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    } 

    public Timestamp getEntryTime() {
        return entry_time;
    }

    public void setEntryTime(Timestamp entry_time) {
        this.entry_time = entry_time;
    }

    public Timestamp getExitTime() {
        return exit_time;
    }

    public void setExitTime(Timestamp exit_time) {
        this.exit_time = exit_time;
    }

    public String getFineReason() {
        return fine_reason;
    }

    public void setFineReason(String fine_reason) {
        this.fine_reason = fine_reason;
    }

    public int getDurationMinute() {
        return duration_minutes;
    }

    public void setDurationMinute(int duration_minutes) {
        this.duration_minutes = duration_minutes;
    }

    public Double getBasePrice() {
        return base_price;
    }

    public void setBasePrice(Double base_price) {
        this.base_price = base_price;
    }

    public Double getFineAmount() {
        return fine_amount;
    }

    public void setFineAmount(Double fine_amount) {
        this.fine_amount = fine_amount;
    }

    public Double getTotalAmount() {
        return total_amount;
    }

    public void setTotalAmount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }
}
