package com.bookmystay.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persists and restores critical application state using Java serialization.
 */
public class PersistenceService {

    public boolean saveState(Path filePath, RoomInventory inventory, BookingHistory bookingHistory) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            SystemStateSnapshot snapshot = new SystemStateSnapshot(
                    inventory.getCurrentAvailability(),
                    bookingHistory.getAllReservations()
            );

            try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                outputStream.writeObject(snapshot);
            }
            return true;
        } catch (IOException ex) {
            System.err.println("Persistence save failed: " + ex.getMessage());
            return false;
        }
    }

    public SystemStateSnapshot loadState(Path filePath) {
        if (!Files.exists(filePath)) {
            System.err.println("Persistence file not found: " + filePath);
            return SystemStateSnapshot.empty();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object obj = inputStream.readObject();
            if (obj instanceof SystemStateSnapshot snapshot) {
                return snapshot;
            }

            System.err.println("Persistence file contains unsupported data. Starting with empty state.");
            return SystemStateSnapshot.empty();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Persistence load failed: " + ex.getMessage());
            return SystemStateSnapshot.empty();
        }
    }
}