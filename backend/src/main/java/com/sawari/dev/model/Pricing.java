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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pricing")
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @Column(nullable = false)
    private Long parkingId;
    private Double ratePerHour;
    private Double overtimeRatePerHour;
    private int gracePeriodMinutes;

    @Enumerated(EnumType.STRING)
    private VehicleModel vehicleType;

}
