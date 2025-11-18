package com.sawari.dev.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entry_id;

    @Column(nullable = false)
    private Long booking_id;

    @Column(nullable = true)
    private Timestamp entry_time;

    @Column(nullable = false)
    private boolean qr_verified = false;

    // Constructors
    public Entry() {}

    public Entry(Long booking_id, Timestamp entry_time, boolean qr_verified) {
        this.booking_id = booking_id;
        this.entry_time = entry_time;
        this.qr_verified = qr_verified;
    }

    // Getters and Setters
    public Long getEntryId() {
        return entry_id;
    }

    public void setEntryId(Long entry_id) {
        this.entry_id = entry_id;
    }

    public Long getBookingId() {
        return booking_id;
    }

    public void setBookingId(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Timestamp getEntryTime() {
        return entry_time;
    }

    public void setEntryTime(Timestamp entry_time) {
        this.entry_time = entry_time;
    }

    public boolean getQrVerified() {
        return qr_verified;
    }

    public void setQrVerified(boolean qr_verified) {
        this.qr_verified = qr_verified;
    }
}
