package com.jaidutta.revolve.exception;

public class RecurringActivityNotFoundException extends Exception {
    public RecurringActivityNotFoundException(String message) {
        super(message);
    }

    public RecurringActivityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
