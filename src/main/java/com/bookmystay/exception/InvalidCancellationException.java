package com.bookmystay.exception;

/**
 * Raised when cancellation cannot be completed safely.
 */
public class InvalidCancellationException extends Exception {

    public InvalidCancellationException(String message) {
        super(message);
    }
}