package com.bookmystay.service;

import com.bookmystay.exception.InvalidBookingException;
import com.bookmystay.model.Reservation;

/**
 * Performs fail-fast validation before booking processing.
 */
public class BookingValidator {

    public void validate(Reservation reservation, RoomInventory inventory) throws InvalidBookingException {
        if (reservation == null) {
            throw new InvalidBookingException("Reservation cannot be null.");
        }
        if (isBlank(reservation.getReservationId())) {
            throw new InvalidBookingException("Reservation ID is required.");
        }
        if (isBlank(reservation.getGuestName())) {
            throw new InvalidBookingException("Guest name is required for reservation " + reservation.getReservationId() + ".");
        }
        if (isBlank(reservation.getRequestedRoomType())) {
            throw new InvalidBookingException("Requested room type is required for reservation " + reservation.getReservationId() + ".");
        }
        if (!inventory.hasRoomType(reservation.getRequestedRoomType())) {
            throw new InvalidBookingException("Invalid room type '" + reservation.getRequestedRoomType() + "' for reservation " + reservation.getReservationId() + ".");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}