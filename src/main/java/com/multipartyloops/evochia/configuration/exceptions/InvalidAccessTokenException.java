package com.multipartyloops.evochia.configuration.exceptions;

public class InvalidAccessTokenException extends RuntimeException{
    public InvalidAccessTokenException(String message){
        super(message);
    }
}
