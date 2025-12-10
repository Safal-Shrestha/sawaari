package com.sawari.dev.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sawari.dev.dbtypes.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "parking_id", nullable = false)
    private Long parkingId;
    
    // numeric DB id -> Long
    @Column(name = "slot_id", nullable = false)
    private Long slotId;
    
    // license plate / external vehicle id -> String
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;
    
    @Column(name = "booking_date_time", nullable = false)
    private LocalDateTime bookingDateTime;
    
    @Column(name = "expected_starting_time", nullable = false)
    private LocalDateTime expectedStartingTime;
    
    @Column(name = "expected_end_time", nullable = false)
    private LocalDateTime expectedEndTime;
    
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;
    
    @PrePersist
    protected void onCreate() {
        if (bookingDateTime == null) {
            bookingDateTime = LocalDateTime.now();
        }
        if (fineAmount == null) {
            fineAmount = BigDecimal.ZERO;
        }
    }
}
