package ru.practicum.shareit.exception;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException() {
        super();
    }

    public AlreadyExistException(String message) {
        super(message);
    }

    public AlreadyExistException(Throwable cause) {
        super(cause);
    }
}
