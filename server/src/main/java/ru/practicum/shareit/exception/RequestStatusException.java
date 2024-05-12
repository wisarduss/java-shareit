package ru.practicum.shareit.exception;

public class RequestStatusException extends RuntimeException {

    public RequestStatusException(String message) {
        super("Unknown state: " + message);
    }

}
