package ru.practicum.shareit.exception;

public class BadRegistrationException extends RuntimeException {

    public BadRegistrationException() {
        super();
    }

    public BadRegistrationException(String message) {
        super(message);
    }

    public BadRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
