package com.bookmystay.app;

import com.bookmystay.model.DoubleRoom;
import com.bookmystay.model.Room;
import com.bookmystay.model.SingleRoom;
import com.bookmystay.model.SuiteRoom;

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
    private static final String APP_VERSION = "v1.1";

    private HotelBookingApplication() {
        // Utility class pattern for entry point holder.
    }

    public static void main(String[] args) {
        System.out.println("Welcome to " + APP_NAME + " " + APP_VERSION);

        // UC2: Object modeling with static availability variables.
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        int singleAvailability = 4;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        System.out.println();
        System.out.println("Available Room Types:");
        System.out.println(singleRoom.getDetails() + " | Available: " + singleAvailability);
        System.out.println(doubleRoom.getDetails() + " | Available: " + doubleAvailability);
        System.out.println(suiteRoom.getDetails() + " | Available: " + suiteAvailability);

        System.out.println();
        System.out.println("Startup complete. Exiting application.");
    }
}
