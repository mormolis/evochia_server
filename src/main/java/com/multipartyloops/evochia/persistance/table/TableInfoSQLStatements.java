package com.multipartyloops.evochia.persistance.table;

public final class TableInfoSQLStatements {
    static final String INSERT_TABLE = "INSERT INTO table_info (table_id, table_alias, group_id, enabled) VALUES (?, ?, ?, ?)";
    static final String DELETE_TABLE_BY_ID = "DELETE FROM table_info WHERE table_id=?";
    static final String GET_ALL_TABLES = "SELECT * FROM table_info";
    static final String SELECT_TABLE_BY_ID = "SELECT * FROM table_info WHERE table_id=?";
    static final String SELECT_TABLE_BY_ALIAS = "SELECT * FROM table_info WHERE table_alias=?";

    private TableInfoSQLStatements() {
    }
}
