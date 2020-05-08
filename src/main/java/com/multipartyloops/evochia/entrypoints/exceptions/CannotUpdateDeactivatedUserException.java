package com.multipartyloops.evochia.entrypoints.exceptions;

public class CannotUpdateDeactivatedUserException extends RuntimeException {
    public CannotUpdateDeactivatedUserException(String message) {
        super(message);
    }
}
