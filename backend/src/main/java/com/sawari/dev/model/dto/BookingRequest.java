package com.sawari.dev.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private LocalDateTime startTime;
    private Long durationMinutes;
    private String vehicleId;
    private Long slotId;
}
