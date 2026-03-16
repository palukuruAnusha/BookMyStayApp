package com.bookmystay.app;

import com.bookmystay.model.DoubleRoom;
import com.bookmystay.model.AddOnService;
import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;
import com.bookmystay.model.Room;
import com.bookmystay.model.SingleRoom;
import com.bookmystay.model.SuiteRoom;
import com.bookmystay.service.AddOnServiceManager;
import com.bookmystay.service.BookingService;
import com.bookmystay.service.BookingRequestQueue;
import com.bookmystay.service.RoomInventory;
import com.bookmystay.service.RoomSearchService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
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
    private static final String APP_VERSION = "v1.6";

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

        // UC5: Accept booking requests and preserve arrival order using FIFO queue.
        BookingRequestQueue requestQueue = new BookingRequestQueue();
        requestQueue.submitRequest(new Reservation("R-1001", "Asha", "Single"));
        requestQueue.submitRequest(new Reservation("R-1002", "Rahul", "Double"));
        requestQueue.submitRequest(new Reservation("R-1003", "Meena", "Suite"));

        System.out.println();
        System.out.println("Queued Booking Requests (FIFO order):");
        for (Reservation reservation : requestQueue.snapshotInArrivalOrder()) {
            System.out.println(reservation);
        }
        System.out.println("Inventory unchanged after request intake: " + inventory.getCurrentAvailability());

        // UC6: Confirm requests, allocate unique room IDs, and decrement inventory.
        BookingService bookingService = new BookingService(inventory);
        List<Reservation> confirmedReservations = new ArrayList<>();
        System.out.println();
        System.out.println("Reservation Confirmations:");
        while (!requestQueue.isEmpty()) {
            Reservation processed = bookingService.processNextRequest(requestQueue);
            if (processed == null) {
                continue;
            }

            if (processed.getStatus() == ReservationStatus.CONFIRMED) {
                System.out.println("Confirmed -> " + processed);
                confirmedReservations.add(processed);
            } else {
                System.out.println("Pending (no availability) -> " + processed);
            }
        }

        System.out.println("Allocated Room IDs: " + bookingService.getAllocatedRoomIds());
        System.out.println("Allocation by Room Type: " + bookingService.getAllocatedByRoomType());
        System.out.println("Inventory after allocation: " + inventory.getCurrentAvailability());

        // UC7: Attach optional services to reservation IDs without mutating core booking state.
        AddOnServiceManager addOnServiceManager = new AddOnServiceManager();
        if (!confirmedReservations.isEmpty()) {
            Reservation first = confirmedReservations.get(0);
            addOnServiceManager.addService(first.getReservationId(), new AddOnService("Breakfast", 15.0));
            addOnServiceManager.addService(first.getReservationId(), new AddOnService("Airport Pickup", 25.0));
        }

        System.out.println();
        System.out.println("Add-On Services by Reservation:");
        for (Reservation reservation : confirmedReservations) {
            List<AddOnService> services = addOnServiceManager.getServices(reservation.getReservationId());
            double additionalCost = addOnServiceManager.calculateAdditionalCost(reservation.getReservationId());
            if (!services.isEmpty()) {
                System.out.println(reservation.getReservationId() + " -> " + services + " | Extra Cost: $" + String.format("%.2f", additionalCost));
            }
        }

        System.out.println();
        System.out.println("Startup complete. Exiting application.");
    }
}
