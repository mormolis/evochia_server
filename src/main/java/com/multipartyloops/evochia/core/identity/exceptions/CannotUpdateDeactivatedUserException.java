package com.multipartyloops.evochia.core.identity.exceptions;

public class CannotUpdateDeactivatedUserException extends RuntimeException {
    public CannotUpdateDeactivatedUserException(String message) {
        super(message);
    }
}
