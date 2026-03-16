package com.bookmystay.service;

import com.bookmystay.model.Reservation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializable snapshot containing inventory and booking history state.
 */
public class SystemStateSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> inventoryState;
    private final List<Reservation> bookingHistory;

    public SystemStateSnapshot(Map<String, Integer> inventoryState, List<Reservation> bookingHistory) {
        this.inventoryState = new HashMap<>(inventoryState);
        this.bookingHistory = new ArrayList<>(bookingHistory);
    }

    public Map<String, Integer> getInventoryState() {
        return Collections.unmodifiableMap(new HashMap<>(inventoryState));
    }

    public List<Reservation> getBookingHistory() {
        return Collections.unmodifiableList(new ArrayList<>(bookingHistory));
    }

    public static SystemStateSnapshot empty() {
        return new SystemStateSnapshot(Collections.emptyMap(), Collections.emptyList());
    }
}