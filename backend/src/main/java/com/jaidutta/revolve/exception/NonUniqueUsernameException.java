package com.jaidutta.revolve.exception;

public class NonUniqueUsernameException extends Exception {
    public NonUniqueUsernameException(String message) { super(message); }
    public NonUniqueUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
