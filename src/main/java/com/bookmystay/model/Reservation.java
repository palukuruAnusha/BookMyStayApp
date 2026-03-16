package com.bookmystay.model;

import java.time.LocalDateTime;

/**
 * Represents a guest booking request and its lifecycle state.
 */
public class Reservation {

    private final String reservationId;
    private final String guestName;
    private final String requestedRoomType;
    private final LocalDateTime requestedAt;
    private ReservationStatus status;
    private String allocatedRoomId;

    public Reservation(String reservationId, String guestName, String requestedRoomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
        this.requestedAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRequestedRoomType() {
        return requestedRoomType;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getAllocatedRoomId() {
        return allocatedRoomId;
    }

    public void setAllocatedRoomId(String allocatedRoomId) {
        this.allocatedRoomId = allocatedRoomId;
    }

    @Override
    public String toString() {
        return String.format("Reservation{id='%s', guest='%s', roomType='%s', status=%s, allocatedRoomId='%s'}",
            reservationId, guestName, requestedRoomType, status, allocatedRoomId);
    }
}
