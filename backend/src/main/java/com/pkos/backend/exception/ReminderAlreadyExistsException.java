package com.pkos.backend.exception;

public class ReminderAlreadyExistsException extends RuntimeException {

    public ReminderAlreadyExistsException(String message) {
        super(message);
    }
}