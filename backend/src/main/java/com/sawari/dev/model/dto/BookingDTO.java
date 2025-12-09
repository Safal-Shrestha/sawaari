package com.sawari.dev.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sawari.dev.dbtypes.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long bookingId;
    private Long userId;
    private Long parkingId;
    private Long slotId;        // Long now
    private String vehicleId;   // license plate string
    private LocalDateTime bookingDateTime;
    private LocalDateTime expectedStartingTime;
    private LocalDateTime expectedEndTime;
    private BigDecimal basePrice;
    private BigDecimal fineAmount;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
}
