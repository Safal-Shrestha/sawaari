package com.sawari.dev.model;

import com.sawari.dev.dbtypes.VehicleModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle")
public class Vehicle {
    @Id
    private String vId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private VehicleModel vModel;
}
