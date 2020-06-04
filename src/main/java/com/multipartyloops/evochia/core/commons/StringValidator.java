package com.multipartyloops.evochia.core.commons;

public class StringValidator {

    public static void throwWhenNull(String string, RuntimeException exception) {
        if(string == null){
            throw exception;
        }
    }

    public static void throwWhenEmpty(String string, RuntimeException exception) {
        if("".equals(string)){
            throw exception;
        }
    }

    public static void throwWhenNullOrEmpty(String string, RuntimeException exception) {
        throwWhenNull(string, exception);
        throwWhenEmpty(string, exception);
    }

}
