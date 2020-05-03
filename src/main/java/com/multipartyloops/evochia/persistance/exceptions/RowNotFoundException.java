package com.multipartyloops.evochia.persistance.exceptions;

import org.springframework.dao.DataAccessException;

public class RowNotFoundException extends DataAccessException {
    public RowNotFoundException(String msg) {
        super(msg);
    }
}
