package com.bookmystay.app;

import com.bookmystay.model.DoubleRoom;
import com.bookmystay.model.Room;
import com.bookmystay.model.SingleRoom;
import com.bookmystay.model.SuiteRoom;
import com.bookmystay.service.RoomInventory;
import com.bookmystay.service.RoomSearchService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Application entry point for the BookMyStay hotel booking system.
 *
 * <p>This initial use case demonstrates deterministic startup behavior and
 * console output using the JVM-invoked main method.</p>
 *
 * @author Nikhil Karur
 * @version 1.0
 */
public final class HotelBookingApplication {

    private static final String APP_NAME = "BookMyStay - Hotel Booking Management System";
    private static final String APP_VERSION = "v1.3";

    private HotelBookingApplication() {
        // Utility class pattern for entry point holder.
    }

    public static void main(String[] args) {
        System.out.println("Welcome to " + APP_NAME + " " + APP_VERSION);

        // UC2: Object modeling.
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        Map<String, Room> roomCatalog = new LinkedHashMap<>();
        roomCatalog.put(singleRoom.getRoomType(), singleRoom);
        roomCatalog.put(doubleRoom.getRoomType(), doubleRoom);
        roomCatalog.put(suiteRoom.getRoomType(), suiteRoom);

        // UC3: Centralized inventory management using a HashMap.
        Map<String, Integer> initialAvailability = new HashMap<>();
        initialAvailability.put("Single", 4);
        initialAvailability.put("Double", 3);
        initialAvailability.put("Suite", 2);
        RoomInventory inventory = new RoomInventory(initialAvailability);

        System.out.println();
        System.out.println("Available Room Types:");

        // UC4: Search only reads inventory and filters out unavailable rooms.
        RoomSearchService roomSearchService = new RoomSearchService();
        List<Room> availableRooms = roomSearchService.findAvailableRooms(roomCatalog, inventory);
        for (Room room : availableRooms) {
            System.out.println(room.getDetails() + " | Available: " + inventory.getAvailability(room.getRoomType()));
        }

        System.out.println();
        System.out.println("Startup complete. Exiting application.");
    }
}
