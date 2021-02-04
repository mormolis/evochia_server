package com.multipartyloops.evochia.core.commons;

public class Preconditions {

    public static void throwWhenNull(Object object, RuntimeException exception) {
        if(object == null){
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
