package ru.practicum.shareit.exception;


public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException() {
        super();
    }


    public IdNotFoundException(String message) {
        super(message);
    }

    public IdNotFoundException(Throwable cause) {
        super(cause);
    }
}
