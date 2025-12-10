package com.sawari.dev.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parking")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parkingId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 350)
    private String location;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private int twoWheelerSpaceCount = 0;
    private int fourWheelerSpaceCount = 0;
    private Double twoWheelerRatePerHour;
    private Double fourWheelerRatePerHour;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false)
    private boolean isActive = true;
    private String imageLink;
}
