package com.bookmystay.service;

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

    public void registerRoomType(String roomType, int count) {
        roomAvailability.put(roomType, Math.max(0, count));
    }
}
