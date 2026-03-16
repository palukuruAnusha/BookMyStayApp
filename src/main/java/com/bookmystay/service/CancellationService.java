package com.bookmystay.service;

import com.bookmystay.exception.InvalidCancellationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Handles cancellation validation and rollback sequencing.
 */
public class CancellationService {

    private final BookingService bookingService;
    private final BookingHistory bookingHistory;
    private final Stack<String> releasedRoomIds = new Stack<>();

    public CancellationService(BookingService bookingService, BookingHistory bookingHistory) {
        this.bookingService = bookingService;
        this.bookingHistory = bookingHistory;
    }

    public void cancelReservation(String reservationId) throws InvalidCancellationException {
        String releasedRoomId = bookingService.cancelReservation(reservationId);
        releasedRoomIds.push(releasedRoomId);
        bookingHistory.markCancelled(reservationId);
    }

    public List<String> getRollbackStackSnapshot() {
        return new ArrayList<>(releasedRoomIds);
    }
}