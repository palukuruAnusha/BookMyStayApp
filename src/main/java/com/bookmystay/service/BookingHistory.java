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

    public synchronized void addConfirmedReservation(Reservation reservation) {
        if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
            history.add(reservation);
        }
    }

    public synchronized List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public synchronized boolean markCancelled(String reservationId) {
        for (Reservation reservation : history) {
            if (reservation.getReservationId().equals(reservationId)) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                return true;
            }
        }
        return false;
    }

    public synchronized void replaceAll(List<Reservation> reservations) {
        history.clear();
        if (reservations != null) {
            history.addAll(reservations);
        }
    }
}
