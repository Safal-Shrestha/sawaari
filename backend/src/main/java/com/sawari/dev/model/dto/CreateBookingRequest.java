package com.sawari.dev.model.dto;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
 
     @NotNull(message = "Parking ID is required")
    private Long parkingId;
    
    @NotNull(message = "Slot ID is required")
    private Long slotId;
    
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime expectedStartingTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime expectedEndTime;
}
