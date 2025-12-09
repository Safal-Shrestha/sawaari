package com.sawari.dev.dbtypes;

public enum BookingStatus {
    CONFIRMED,      // Booking is confirmed but not checked in yet
    CHECKED_IN,     // User has checked in
    COMPLETED,      // Booking completed successfully
    CANCELLED       // Booking cancelled by user
}