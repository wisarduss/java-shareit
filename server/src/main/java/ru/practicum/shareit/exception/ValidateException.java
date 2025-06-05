package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {

    public ValidateException() {
        super();
    }

    public ValidateException(String message) {
        super(message);
    }
}
