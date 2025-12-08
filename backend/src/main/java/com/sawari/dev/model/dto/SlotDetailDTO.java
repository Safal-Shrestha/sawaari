package com.sawari.dev.model.dto;

import com.sawari.dev.dbtypes.VehicleModel; // Import the enum

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDetailDTO {
    private Long slotId;           
    private Long parkingId;  
    private Long slotNumber;       
    private VehicleModel slotType; 
    private boolean isOccupied;
    private boolean isReserved;
    private String status;
}