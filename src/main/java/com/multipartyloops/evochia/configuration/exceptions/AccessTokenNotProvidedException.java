package com.multipartyloops.evochia.configuration.exceptions;

public class AccessTokenNotProvidedException extends RuntimeException {
    public AccessTokenNotProvidedException(String message){
        super(message);
    }
}
