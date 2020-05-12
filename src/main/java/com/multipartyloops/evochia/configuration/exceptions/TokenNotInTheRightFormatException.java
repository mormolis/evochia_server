package com.multipartyloops.evochia.configuration.exceptions;

public class TokenNotInTheRightFormatException extends RuntimeException{
    public TokenNotInTheRightFormatException(String message){
        super(message);
    }
}
