package com.multipartyloops.evochia.persistance.table.grouping;

import java.util.List;
import java.util.Optional;

public interface TableGroupingRepository<T> {

    void insertTableGroup(T group);

    void deleteTableGroup(String groupId);

    List<T> getAllTableGroups();

    Optional<T> getTableGroupById(String groupId);

    Optional<T> getTableGroupByName(String groupName);
}
