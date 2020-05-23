package com.multipartyloops.evochia.core.product.exceptions;

public class MandatoryFieldNotPassedException extends RuntimeException{

    public MandatoryFieldNotPassedException(String message) {
        super(message);
    }
}
