package com.sawari.dev.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequest {
    @NotNull
    private Long parkingId;

    // license plate (string) kept in Booking; frontend still passes vehicle id (string)
    @NotNull
    private String vehicleId;

    @NotNull
    private String expectedStart; // ISO datetime, e.g. 2025-12-09T15:00:00

    @NotNull
    private String expectedEnd;
}