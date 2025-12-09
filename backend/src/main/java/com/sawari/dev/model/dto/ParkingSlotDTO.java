package com.sawari.dev.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotDTO {
    private Long id;
    private String location;
    private String address;
    private String imageLink;
    private boolean active;
    private int totalSlots;
    private int availableSlots;
    private int occupiedSlots;
    private int reservedSlots;
}