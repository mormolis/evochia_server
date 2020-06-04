package com.multipartyloops.evochia.persistance.terminal;

public final class TerminalsSQLStatements {
    public static final String TERMINALS_INSERTION = "INSERT into terminals (terminal_id, name) VALUES (?, ?)";
    public static final String TERMINALS_DELETE_BY_ID = "DELETE FROM terminals WHERE terminal_id=?";
    public static final String TERMINALS_UPDATE = "UPDATE terminals SET name=? WHERE terminal_id=?";
    public static final String TERMINALS_SELECT_ALL = "SELECT * FROM terminals";
    public static final String TERMINALS_SELECT_BY_ID = "SELECT * FROM terminals WHERE terminal_id=?";

    public TerminalsSQLStatements() {
    }
}
