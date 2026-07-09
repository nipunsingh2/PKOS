package com.pkos.backend.exception;

public class EmailAlreadyExistsException extends DuplicateResourceException {

    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}