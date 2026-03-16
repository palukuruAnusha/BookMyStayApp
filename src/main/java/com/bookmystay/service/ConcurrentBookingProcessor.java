package com.bookmystay.service;

import com.bookmystay.model.Reservation;
import com.bookmystay.model.ReservationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates concurrent booking processing using multiple worker threads.
 */
public class ConcurrentBookingProcessor {

    private final BookingService bookingService;
    private final BookingHistory bookingHistory;

    public ConcurrentBookingProcessor(BookingService bookingService, BookingHistory bookingHistory) {
        this.bookingService = bookingService;
        this.bookingHistory = bookingHistory;
    }

    public List<Reservation> process(BookingRequestQueue sharedQueue, int workerCount) {
        List<Reservation> processed = Collections.synchronizedList(new ArrayList<>());
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < workerCount; i++) {
            Thread worker = new Thread(() -> {
                while (true) {
                    Reservation result = bookingService.processNextRequest(sharedQueue);
                    if (result == null) {
                        break;
                    }
                    processed.add(result);
                    if (result.getStatus() == ReservationStatus.CONFIRMED) {
                        bookingHistory.addConfirmedReservation(result);
                    }
                }
            }, "booking-worker-" + (i + 1));
            workers.add(worker);
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return new ArrayList<>(processed);
    }
}