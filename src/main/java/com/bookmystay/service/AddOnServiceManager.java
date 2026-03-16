package com.bookmystay.service;

import com.bookmystay.model.AddOnService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains reservation to add-on service associations.
 */
public class AddOnServiceManager {

    private final Map<String, List<AddOnService>> addOnsByReservationId = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        addOnsByReservationId.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public List<AddOnService> getServices(String reservationId) {
        return addOnsByReservationId.containsKey(reservationId)
                ? Collections.unmodifiableList(addOnsByReservationId.get(reservationId))
                : Collections.emptyList();
    }

    public double calculateAdditionalCost(String reservationId) {
        double total = 0.0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getPrice();
        }
        return total;
    }

    public Map<String, List<AddOnService>> snapshot() {
        Map<String, List<AddOnService>> copy = new HashMap<>();
        for (Map.Entry<String, List<AddOnService>> entry : addOnsByReservationId.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}
