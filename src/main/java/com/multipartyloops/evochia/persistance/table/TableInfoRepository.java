package com.multipartyloops.evochia.persistance.table;

import java.util.List;
import java.util.Optional;

public interface TableInfoRepository<T> {
    void insertTable(T table);

    void deleteTable(String tableId);

    List<T> getAllTables();

    Optional<T> getTableById(String tableId);

    Optional<T> getTableByAlias(String tableAlias);
}
