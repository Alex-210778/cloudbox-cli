package com.lukyanovich.cloudbox.exception;

public class UserServiceException extends RuntimeException {

    public UserServiceException(Throwable cause) {
        super(cause);
    }

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
