package com.bookmystay.service;

import com.bookmystay.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Read-only room search service that exposes only currently available room types.
 */
public class RoomSearchService {

    public List<Room> findAvailableRooms(Map<String, Room> roomCatalog, RoomInventory inventory) {
        List<Room> availableRooms = new ArrayList<>();

        for (Map.Entry<String, Room> entry : roomCatalog.entrySet()) {
            if (inventory.getAvailability(entry.getKey()) > 0) {
                availableRooms.add(entry.getValue());
            }
        }

        return availableRooms;
    }
}
