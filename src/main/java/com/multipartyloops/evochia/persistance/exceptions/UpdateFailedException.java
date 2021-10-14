package com.multipartyloops.evochia.persistance.exceptions;

public class UpdateFailedException extends RuntimeException {
    public UpdateFailedException(String msg) {
        super(msg);
    }
}
