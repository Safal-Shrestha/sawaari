package com.sawari.dev.service;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sawari.dev.model.Booking;
import com.sawari.dev.model.dto.BookingDetailsDTO;
import com.sawari.dev.repository.BookingRepository;
import com.sawari.dev.repository.SlotRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookingDetailService {
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;

    public List<BookingDetailsDTO> findBookingsForUser(Long userId) {
        List<Booking> booking = bookingRepository.findByUserId(userId);

        List<BookingDetailsDTO> results = new ArrayList<>();

        for (Booking b: booking) {
            String location = slotRepository.getParkingNameBySlotId(b.getSlotId());
            if (location == null) {
                location = "Unknown Location"; 
            }

            Long startMillis = b.getExpectedStartingTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();

            Long endMillis = b.getExpectedEndTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();

            Long duration = Duration.between(b.getExpectedStartingTime(), b.getExpectedEndTime())
                .toMinutes();

            
            BookingDetailsDTO dto = new BookingDetailsDTO(
                    startMillis,
                    endMillis,
                    duration,
                    location,
                    b.getBookingStatus()
            );

            results.add(dto);
        }
        return results;
    }
}
