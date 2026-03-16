package com.bookmystay.service;

import com.bookmystay.exception.InvalidBookingException;
import com.bookmystay.exception.InvalidCancellationException;
import com.bookmystay.exception.InventoryStateException;
import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles reservation confirmation and safe room allocation.
 */
public class BookingService {

    private final RoomInventory inventory;
    private final BookingValidator validator;
    private final Set<String> allocatedRoomIds = new HashSet<>();
    private final Map<String, Set<String>> allocatedByRoomType = new HashMap<>();
    private final Map<String, Integer> roomTypeAllocationCounter = new HashMap<>();
    private final Map<String, Reservation> confirmedReservationsById = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.validator = new BookingValidator();
    }

    public Reservation processNextRequest(BookingRequestQueue queue) {
        Reservation next = queue.pollNextRequest();
        if (next == null) {
            return null;
        }

        try {
            validator.validate(next, inventory);
            inventory.validateState();
        } catch (InvalidBookingException | InventoryStateException ex) {
            next.setStatus(ReservationStatus.FAILED);
            next.setFailureReason(ex.getMessage());
            return next;
        }

        String roomType = next.getRequestedRoomType();
        if (!inventory.decrementAvailability(roomType)) {
            next.setFailureReason("No availability for room type '" + roomType + "'.");
            return next;
        }

        String roomId = generateUniqueRoomId(roomType);
        allocatedRoomIds.add(roomId);
        allocatedByRoomType.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

        next.setAllocatedRoomId(roomId);
        next.setStatus(ReservationStatus.CONFIRMED);
        next.setFailureReason(null);
        confirmedReservationsById.put(next.getReservationId(), next);
        return next;
    }

    public String cancelReservation(String reservationId) throws InvalidCancellationException {
        Reservation reservation = confirmedReservationsById.get(reservationId);
        if (reservation == null) {
            throw new InvalidCancellationException("Reservation '" + reservationId + "' does not exist or was never confirmed.");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidCancellationException("Reservation '" + reservationId + "' is already cancelled.");
        }

        String roomType = reservation.getRequestedRoomType();
        String roomId = reservation.getAllocatedRoomId();
        if (roomId == null || !allocatedRoomIds.contains(roomId)) {
            throw new InvalidCancellationException("Allocated room ID for reservation '" + reservationId + "' is not active.");
        }

        allocatedRoomIds.remove(roomId);
        Set<String> typeAllocations = allocatedByRoomType.get(roomType);
        if (typeAllocations != null) {
            typeAllocations.remove(roomId);
            if (typeAllocations.isEmpty()) {
                allocatedByRoomType.remove(roomType);
            }
        }

        if (!inventory.incrementAvailability(roomType)) {
            throw new InvalidCancellationException("Failed to restore inventory for room type '" + roomType + "'.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return roomId;
    }

    public Set<String> getAllocatedRoomIds() {
        return Collections.unmodifiableSet(allocatedRoomIds);
    }

    public Map<String, Set<String>> getAllocatedByRoomType() {
        Map<String, Set<String>> copy = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : allocatedByRoomType.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    private String generateUniqueRoomId(String roomType) {
        int sequence = roomTypeAllocationCounter.getOrDefault(roomType, 0) + 1;
        roomTypeAllocationCounter.put(roomType, sequence);

        String normalizedType = roomType.toUpperCase();
        String roomId = normalizedType + "-" + String.format("%03d", sequence);
        while (allocatedRoomIds.contains(roomId)) {
            sequence++;
            roomTypeAllocationCounter.put(roomType, sequence);
            roomId = normalizedType + "-" + String.format("%03d", sequence);
        }
        return roomId;
    }
}
