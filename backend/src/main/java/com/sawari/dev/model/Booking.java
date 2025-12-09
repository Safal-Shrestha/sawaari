package com.sawari.dev.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sawari.dev.dbtypes.BookingStatus;

@Entity
@Table(name = "bookings")
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
    
    @Column(name = "slot_id", nullable = false)
    private String slotId;
    
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    
    @Column(name = "booking_date_time", nullable = false)
    private LocalDateTime bookingDateTime;
    
    @Column(name = "expected_starting_time", nullable = false)
    private LocalDateTime expectedStartingTime;
    
    @Column(name = "expected_end_time", nullable = false)
    private LocalDateTime expectedEndTime;
    
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;
    
    @Column(name = "fine_amount")
    private BigDecimal fineAmount;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;
    
    @PrePersist
    protected void onCreate() {
        if (bookingDateTime == null) {
            bookingDateTime = LocalDateTime.now();
        }
    }
    
    
private LocalDateTime checkOutTime;
private LocalDateTime checkInTime;

public LocalDateTime getCheckOutTime() {
    return checkOutTime;
}

public void setCheckOutTime(LocalDateTime checkOutTime) {
    this.checkOutTime = checkOutTime;
}

public LocalDateTime getCheckInTime() {
    return checkInTime;
}

public void setCheckInTime(LocalDateTime checkInTime) {
    this.checkInTime = checkInTime;
}

}