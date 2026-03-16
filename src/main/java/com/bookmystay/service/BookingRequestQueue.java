package com.bookmystay.service;

import com.bookmystay.model.Reservation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * FIFO queue for incoming booking requests.
 */
public class BookingRequestQueue {

    private final Queue<Reservation> queue = new ArrayDeque<>();

    public void submitRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public Reservation pollNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public List<Reservation> snapshotInArrivalOrder() {
        return new ArrayList<>(queue);
    }
}
