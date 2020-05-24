package com.multipartyloops.evochia.core.commons;

import java.util.UUID;

public class UUIDFormatChecker {

    public static void confirmOrThrow(String toParse, RuntimeException throwable) {
        try{
            UUID.fromString(toParse);
        } catch (IllegalArgumentException e){
            throw throwable;
        }
    }
}
