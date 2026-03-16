package com.bookmystay.exception;

/**
 * Raised when a booking request fails domain validation.
 */
public class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}