package com.pkos.backend.exception;

public class UserAlreadyExistsException extends DuplicateResourceException {

    public UserAlreadyExistsException() {
        super("Username already exists");
    }
}