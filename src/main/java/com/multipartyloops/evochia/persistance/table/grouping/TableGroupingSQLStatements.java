package com.multipartyloops.evochia.persistance.table.grouping;

public final class TableGroupingSQLStatements {

    static final String TABLE_GROUPING_INSERTION = "INSERT INTO table_grouping (group_id, group_name, enabled) VALUES (?, ?, ?)";
    static final String DELETE_TABLE_GROUPING = "DELETE FROM table_grouping WHERE group_id=?";
    static final String SELECT_ALL_TABLE_GROUPS = "SELECT * FROM table_grouping";
    static final String SELECT_TABLE_GROUP_BY_ID = "SELECT * FROM table_grouping WHERE group_id=?";
    static final String SELECT_GROUP_NAME_BY_ID = "SELECT * FROM table_grouping WHERE group_name=?";

    private TableGroupingSQLStatements() {
    }
}
