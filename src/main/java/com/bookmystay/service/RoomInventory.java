package com.bookmystay.service;

import com.bookmystay.exception.InventoryStateException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized inventory state holder for room availability.
 */
public class RoomInventory {

    private final Map<String, Integer> roomAvailability;

    public RoomInventory(Map<String, Integer> initialAvailability) {
        this.roomAvailability = new HashMap<>(initialAvailability);
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    public boolean hasRoomType(String roomType) {
        return roomAvailability.containsKey(roomType);
    }

    public Map<String, Integer> getCurrentAvailability() {
        return Collections.unmodifiableMap(roomAvailability);
    }

    public boolean updateAvailability(String roomType, int newCount) {
        if (!roomAvailability.containsKey(roomType) || newCount < 0) {
            return false;
        }
        roomAvailability.put(roomType, newCount);
        return true;
    }

    public boolean decrementAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current <= 0) {
            return false;
        }
        roomAvailability.put(roomType, current - 1);
        return true;
    }

    public boolean incrementAvailability(String roomType) {
        if (!roomAvailability.containsKey(roomType)) {
            return false;
        }
        roomAvailability.put(roomType, getAvailability(roomType) + 1);
        return true;
    }

    public void registerRoomType(String roomType, int count) {
        roomAvailability.put(roomType, Math.max(0, count));
    }

    public void validateState() throws InventoryStateException {
        for (Map.Entry<String, Integer> entry : roomAvailability.entrySet()) {
            if (entry.getValue() == null || entry.getValue() < 0) {
                throw new InventoryStateException("Invalid inventory count for room type '" + entry.getKey() + "'.");
            }
        }
    }
}
