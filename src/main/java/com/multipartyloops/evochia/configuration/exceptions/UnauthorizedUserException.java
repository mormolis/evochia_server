package com.multipartyloops.evochia.configuration.exceptions;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message){
        super(message);
    }
}
