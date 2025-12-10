package com.sawari.dev.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import com.sawari.dev.dbtypes.BookingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Immutable
@Table(name = "analysis")
public class Analysis {
    @Id
    private Long bookingId;

    private Long userId;
    private Long parkingId;
    private Long slotId;
    private String vehicleId;
    private LocalDateTime bookingDateTime;
    private LocalDateTime expectedStartingTime;
    private LocalDateTime expectedEndTime;
    private Double fineAmount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
}
