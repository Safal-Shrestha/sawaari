package com.sawari.dev.model;

import java.sql.Timestamp;

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
@Table(name = "analysis")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @Column(nullable = false)
    private Long bookingId;
    private Long userId;
    private Long parkingId;
    private Long slotId;
    private String vehicleId;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private String fineReason;
    private int  durationMinutes;
    private Double basePrice;
    private Double fineAmount;
    private Double totalAmount;
    private Timestamp createdAt;
}
