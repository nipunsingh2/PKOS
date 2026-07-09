package com.pkos.backend.exception;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super("User not found");
    }
}