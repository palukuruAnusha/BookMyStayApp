package com.bookmystay.service;

import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;

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
        int confirmedCount = 0;
        int cancelledCount = 0;

        for (Reservation reservation : reservations) {
            roomTypeCounts.put(
                    reservation.getRequestedRoomType(),
                    roomTypeCounts.getOrDefault(reservation.getRequestedRoomType(), 0) + 1
            );

            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                confirmedCount++;
            } else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                cancelledCount++;
            }
        }

        return "Total Historical Bookings: " + reservations.size() + System.lineSeparator()
                + "Active Confirmed Bookings: " + confirmedCount + System.lineSeparator()
                + "Cancelled Bookings: " + cancelledCount + System.lineSeparator()
                + "By Room Type: " + roomTypeCounts;
    }
}
