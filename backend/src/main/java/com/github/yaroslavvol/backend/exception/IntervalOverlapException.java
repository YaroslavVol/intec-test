package com.github.yaroslavvol.backend.exception;

public class IntervalOverlapException extends RuntimeException {

    public IntervalOverlapException(String message) {
        super(message);
    }

    public IntervalOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
