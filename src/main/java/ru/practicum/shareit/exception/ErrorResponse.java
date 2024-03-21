package ru.practicum.shareit.exception;

import lombok.Builder;

@Builder
public class ErrorResponse {
    private String message;

    public String stackTrace;

    public ErrorResponse(String message, String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}
