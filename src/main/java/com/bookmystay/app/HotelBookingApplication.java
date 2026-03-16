package com.bookmystay.app;

import com.bookmystay.exception.InvalidCancellationException;
import com.bookmystay.model.DoubleRoom;
import com.bookmystay.model.AddOnService;
import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;
import com.bookmystay.model.Room;
import com.bookmystay.model.SingleRoom;
import com.bookmystay.model.SuiteRoom;
import com.bookmystay.service.AddOnServiceManager;
import com.bookmystay.service.BookingService;
import com.bookmystay.service.BookingHistory;
import com.bookmystay.service.BookingReportService;
import com.bookmystay.service.BookingRequestQueue;
import com.bookmystay.service.CancellationService;
import com.bookmystay.service.ConcurrentBookingProcessor;
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
    private static final String APP_VERSION = "v1.11";

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
        requestQueue.submitRequest(new Reservation("R-1004", "", "Single"));
        requestQueue.submitRequest(new Reservation("R-1005", "Ishaan", "Penthouse"));

        System.out.println();
        System.out.println("Queued Booking Requests (FIFO order):");
        for (Reservation reservation : requestQueue.snapshotInArrivalOrder()) {
            System.out.println(reservation);
        }
        System.out.println("Inventory unchanged after request intake: " + inventory.getCurrentAvailability());

        // UC6: Confirm requests, allocate unique room IDs, and decrement inventory.
        BookingService bookingService = new BookingService(inventory);
        BookingHistory bookingHistory = new BookingHistory();
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
                bookingHistory.addConfirmedReservation(processed);
            } else if (processed.getStatus() == ReservationStatus.FAILED) {
                System.out.println("Failed -> " + processed.getReservationId() + " | Reason: " + processed.getFailureReason());
            } else {
                System.out.println("Pending -> " + processed.getReservationId() + " | Reason: " + processed.getFailureReason());
            }
        }

        System.out.println("Allocated Room IDs: " + bookingService.getAllocatedRoomIds());
        System.out.println("Allocation by Room Type: " + bookingService.getAllocatedByRoomType());
        System.out.println("Inventory after allocation: " + inventory.getCurrentAvailability());

        // UC10: Cancellation with rollback stack and inventory restoration.
        CancellationService cancellationService = new CancellationService(bookingService, bookingHistory);
        System.out.println();
        System.out.println("Cancellation Operations:");
        if (!confirmedReservations.isEmpty()) {
            String reservationToCancel = confirmedReservations.get(0).getReservationId();
            try {
                cancellationService.cancelReservation(reservationToCancel);
                System.out.println("Cancelled -> " + reservationToCancel);
            } catch (InvalidCancellationException ex) {
                System.out.println("Cancellation Failed -> " + ex.getMessage());
            }

            try {
                cancellationService.cancelReservation(reservationToCancel);
                System.out.println("Cancelled -> " + reservationToCancel);
            } catch (InvalidCancellationException ex) {
                System.out.println("Cancellation Failed -> " + ex.getMessage());
            }
        }

        try {
            cancellationService.cancelReservation("R-9999");
            System.out.println("Cancelled -> R-9999");
        } catch (InvalidCancellationException ex) {
            System.out.println("Cancellation Failed -> " + ex.getMessage());
        }

        System.out.println("Rollback Stack (released room IDs): " + cancellationService.getRollbackStackSnapshot());
        System.out.println("Inventory after cancellation attempts: " + inventory.getCurrentAvailability());

        // UC11: Concurrent request simulation with synchronized critical sections.
        Map<String, Integer> concurrentAvailability = new HashMap<>();
        concurrentAvailability.put("Single", 2);
        concurrentAvailability.put("Double", 1);
        concurrentAvailability.put("Suite", 1);

        RoomInventory concurrentInventory = new RoomInventory(concurrentAvailability);
        BookingRequestQueue concurrentQueue = new BookingRequestQueue();
        concurrentQueue.submitRequest(new Reservation("CR-2001", "Guest-1", "Single"));
        concurrentQueue.submitRequest(new Reservation("CR-2002", "Guest-2", "Single"));
        concurrentQueue.submitRequest(new Reservation("CR-2003", "Guest-3", "Single"));
        concurrentQueue.submitRequest(new Reservation("CR-2004", "Guest-4", "Double"));
        concurrentQueue.submitRequest(new Reservation("CR-2005", "Guest-5", "Suite"));
        concurrentQueue.submitRequest(new Reservation("CR-2006", "Guest-6", "Suite"));

        BookingService concurrentBookingService = new BookingService(concurrentInventory);
        BookingHistory concurrentHistory = new BookingHistory();
        ConcurrentBookingProcessor concurrentProcessor = new ConcurrentBookingProcessor(concurrentBookingService, concurrentHistory);
        List<Reservation> concurrentResults = concurrentProcessor.process(concurrentQueue, 3);

        int concurrentConfirmed = 0;
        for (Reservation reservation : concurrentResults) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                concurrentConfirmed++;
            }
        }

        System.out.println();
        System.out.println("Concurrent Booking Simulation:");
        System.out.println("Processed Requests: " + concurrentResults.size());
        System.out.println("Confirmed Requests: " + concurrentConfirmed);
        System.out.println("Unique Allocated Room IDs: " + concurrentBookingService.getAllocatedRoomIds().size());
        System.out.println("Concurrent Inventory After Processing: " + concurrentInventory.getCurrentAvailability());

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

        // UC8: Historical tracking and report generation without mutating booking history.
        BookingReportService reportService = new BookingReportService();
        System.out.println();
        System.out.println("Booking History (Insertion Order):");
        for (Reservation reservation : bookingHistory.getAllReservations()) {
            System.out.println(reservation);
        }
        System.out.println("Summary Report:");
        System.out.println(reportService.generateSummaryReport(bookingHistory));

        System.out.println();
        System.out.println("Startup complete. Exiting application.");
    }
}
