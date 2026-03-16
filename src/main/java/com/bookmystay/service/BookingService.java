package com.bookmystay.service;

import com.bookmystay.exception.InvalidBookingException;
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
        return next;
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
