package com.sawari.dev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parking")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parking_id;

    @Column(nullable = false)
    private Long owner_id;

    @Column(nullable = false, length = 350)
    private String location;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private int two_wheeler_space_count = 0;

    @Column(nullable = false)
    private int four_wheeler_space_count = 0;

    @Column(nullable = false)
    private boolean is_active = true;
    private String image_link;

    public Parking() {}

    public Parking(Long owner_id, String location, String address,
                   int two_wheeler_space_count, int four_wheeler_space_count, boolean is_active, String image_link) {
        this.owner_id = owner_id;
        this.location = location;
        this.address = address;
        this.two_wheeler_space_count = two_wheeler_space_count;
        this.four_wheeler_space_count = four_wheeler_space_count;
        this.is_active = is_active;
        this.image_link = image_link;
    }

    // Getters and Setters
    public Long getParkingId() {
        return parking_id;
    }

    public void setParkingId(Long parking_id) {
        this.parking_id = parking_id;
    }

    public Long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(Long owner_id) {
        this.owner_id = owner_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTwoWheelerSpaceCount() {
        return two_wheeler_space_count;
    }

    public void setTwoWheelerSpaceCount(int two_wheeler_space_count) {
        this.two_wheeler_space_count = two_wheeler_space_count;
    }

    public int getFourWheelerSpaceCount() {
        return four_wheeler_space_count;
    }

    public void setFourWheelerSpaceCount(int four_wheeler_space_count) {
        this.four_wheeler_space_count = four_wheeler_space_count;
    }

    public boolean isActive() {
        return is_active;
    }

    public void setActive(boolean is_active) {
        this.is_active = is_active;
    }

    public String getImageLink() {
        return image_link;
    }

    public void setImageLink(String image_link) {
        this.image_link = image_link;
    }
}
