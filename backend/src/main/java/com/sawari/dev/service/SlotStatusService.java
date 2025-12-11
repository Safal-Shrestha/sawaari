package com.sawari.dev.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sawari.dev.model.Slot;
import com.sawari.dev.repository.BookingRepository;
import com.sawari.dev.repository.SlotRepository;

@Service
public class SlotStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(SlotStatusService.class);
    
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    
    // Nepal timezone
    private static final ZoneId NEPAL_TIMEZONE = ZoneId.of("Asia/Kathmandu");
    
    public SlotStatusService(SlotRepository slotRepository, BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }
    
    // Run every 5 minutes to update slot reservation status
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    @Transactional
    public void updateSlotReservations() {
        logger.debug("Running slot status update for Nepal timezone");
        LocalDateTime now = LocalDateTime.now(NEPAL_TIMEZONE);
        
        // Get slots that are currently reserved
        List<Slot> reservedSlots = slotRepository.findByIsReservedTrue();
        int unreservedCount = 0;
        
        for (Slot slot : reservedSlots) {
            List<com.sawari.dev.model.Booking> upcomingBookings = bookingRepository.findUpcomingBookingsForSlot(
                    slot.getSlotId(), now);
            
            boolean shouldBeReserved = false;
            for (com.sawari.dev.model.Booking booking : upcomingBookings) {
                LocalDateTime bookingStart = booking.getExpectedStartingTime();
                long minutesUntilStart = java.time.Duration.between(now, bookingStart).toMinutes();
                if (minutesUntilStart <= 30 && minutesUntilStart >= 0) {
                    shouldBeReserved = true;
                    break;
                }
            }
            
            if (!shouldBeReserved) {
                slot.setIsReserved(false);
                slotRepository.save(slot);
                unreservedCount++;
                logger.debug("Unreserved slot {} in Nepal timezone", slot.getSlotId());
            }
        }
        
        // Get slots with bookings starting within next 35 minutes
        LocalDateTime timeThreshold = now.plusMinutes(35);
        List<Long> slotIdsWithUpcomingBookings = bookingRepository.findSlotIdsWithBookingsStartingBefore(timeThreshold);
        
        if (!slotIdsWithUpcomingBookings.isEmpty()) {
            List<Slot> unreservedSlotsWithBookings = slotRepository.findBySlotIdInAndIsReservedFalse(slotIdsWithUpcomingBookings);
            int reservedCount = 0;
            
            for (Slot slot : unreservedSlotsWithBookings) {
                List<com.sawari.dev.model.Booking> upcomingBookings = bookingRepository.findUpcomingBookingsForSlot(
                        slot.getSlotId(), now);
                
                for (com.sawari.dev.model.Booking booking : upcomingBookings) {
                    LocalDateTime bookingStart = booking.getExpectedStartingTime();
                    long minutesUntilStart = java.time.Duration.between(now, bookingStart).toMinutes();
                    if (minutesUntilStart <= 30 && minutesUntilStart >= 0) {
                        slot.setIsReserved(true);
                        slotRepository.save(slot);
                        reservedCount++;
                        logger.debug("Reserved slot {} in Nepal timezone (starts in {} minutes)", 
                                slot.getSlotId(), minutesUntilStart);
                        break;
                    }
                }
            }
            
            if (reservedCount > 0 || unreservedCount > 0) {
                logger.info("Nepal timezone slot update: {} reserved, {} unreserved", 
                        reservedCount, unreservedCount);
            }
        } else if (unreservedCount > 0) {
            logger.info("Nepal timezone slot update: {} slots unreserved", unreservedCount);
        }
    }
}