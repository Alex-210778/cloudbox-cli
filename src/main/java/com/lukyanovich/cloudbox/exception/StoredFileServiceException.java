package com.lukyanovich.cloudbox.exception;

public class StoredFileServiceException extends RuntimeException {

    public StoredFileServiceException(Throwable cause) {
        super(cause);
    }

    public StoredFileServiceException(String message) {
        super(message);
    }

    public StoredFileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
