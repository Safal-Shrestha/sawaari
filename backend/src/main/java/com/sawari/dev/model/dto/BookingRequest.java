package com.sawari.dev.model.dto;

import com.sawari.dev.dbtypes.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private long startTimestamp;
    private long endTimestamp;
    private long durationMinutes;
    private String location;
    private BookingStatus status;
}
