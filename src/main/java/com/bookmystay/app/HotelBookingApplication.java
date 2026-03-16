package com.bookmystay.app;

import com.bookmystay.model.DoubleRoom;
import com.bookmystay.model.Room;
import com.bookmystay.model.SingleRoom;
import com.bookmystay.model.SuiteRoom;
import com.bookmystay.service.RoomInventory;

import java.util.HashMap;
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
    private static final String APP_VERSION = "v1.2";

    private HotelBookingApplication() {
        // Utility class pattern for entry point holder.
    }

    public static void main(String[] args) {
        System.out.println("Welcome to " + APP_NAME + " " + APP_VERSION);

        // UC2: Object modeling.
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // UC3: Centralized inventory management using a HashMap.
        Map<String, Integer> initialAvailability = new HashMap<>();
        initialAvailability.put("Single", 4);
        initialAvailability.put("Double", 3);
        initialAvailability.put("Suite", 2);
        RoomInventory inventory = new RoomInventory(initialAvailability);

        System.out.println();
        System.out.println("Available Room Types:");
        System.out.println(singleRoom.getDetails() + " | Available: " + inventory.getAvailability("Single"));
        System.out.println(doubleRoom.getDetails() + " | Available: " + inventory.getAvailability("Double"));
        System.out.println(suiteRoom.getDetails() + " | Available: " + inventory.getAvailability("Suite"));

        System.out.println();
        System.out.println("Startup complete. Exiting application.");
    }
}
