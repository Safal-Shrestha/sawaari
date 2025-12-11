package com.sawari.dev.model.dto;

import com.sawari.dev.dbtypes.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailsDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private Long durationMinutes;
    private String location;
    private BookingStatus status;
}
