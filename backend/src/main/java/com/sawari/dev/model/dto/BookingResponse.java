package com.sawari.dev.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
  
    // Core booking info
    private Long bookingId;
    private Long userId;
    private Long parkingId;
    private Long slotId;
    private Long vehicleId;
    
    // Time info
    private LocalDateTime bookingDateTime;
    private LocalDateTime expectedStartingTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    
    // Status
    private String bookingStatus;  // or String if you prefer
    
    // Cost info
    private BigDecimal basePrice;
    private BigDecimal totalAmount;
    private BigDecimal fineAmount;
}
