package com.bookmystay.exception;

/**
 * Raised when room inventory is found in an invalid state.
 */
public class InventoryStateException extends Exception {

    public InventoryStateException(String message) {
        super(message);
    }
}