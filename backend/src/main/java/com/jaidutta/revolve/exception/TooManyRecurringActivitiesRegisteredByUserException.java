package com.jaidutta.revolve.exception;

public class TooManyRecurringActivitiesRegisteredByUserException extends Exception {
    public TooManyRecurringActivitiesRegisteredByUserException(String message) {
        super(message);
    }

    public TooManyRecurringActivitiesRegisteredByUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
