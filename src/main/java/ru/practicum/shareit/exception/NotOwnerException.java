package ru.practicum.shareit.exception;

public class NotOwnerException extends RuntimeException {

    public NotOwnerException() {
        super();
    }

    public NotOwnerException(String message) {
        super(message);
    }

    public NotOwnerException(Throwable cause) {
        super(cause);
    }
}
