package com.bookmystay.service;

import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered in-memory history of confirmed bookings.
 */
public class BookingHistory {

    private final List<Reservation> history = new ArrayList<>();

    public void addConfirmedReservation(Reservation reservation) {
        if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
            history.add(reservation);
        }
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}
