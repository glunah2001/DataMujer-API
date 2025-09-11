package com.UNED.APIDataMujer.exception;

public class NotActiveUserException extends RuntimeException {
    public NotActiveUserException(String message) {
        super(message);
    }
}
