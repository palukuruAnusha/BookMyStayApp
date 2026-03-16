package com.bookmystay.service;

import com.bookmystay.model.Reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates read-only reports from booking history.
 */
public class BookingReportService {

    public String generateSummaryReport(BookingHistory bookingHistory) {
        List<Reservation> reservations = bookingHistory.getAllReservations();
        Map<String, Integer> roomTypeCounts = new HashMap<>();

        for (Reservation reservation : reservations) {
            roomTypeCounts.put(
                    reservation.getRequestedRoomType(),
                    roomTypeCounts.getOrDefault(reservation.getRequestedRoomType(), 0) + 1
            );
        }

        return "Total Confirmed Bookings: " + reservations.size() + System.lineSeparator()
                + "By Room Type: " + roomTypeCounts;
    }
}
