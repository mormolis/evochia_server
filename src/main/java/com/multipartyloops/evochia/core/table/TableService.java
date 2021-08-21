package com.multipartyloops.evochia.core.table;

import com.multipartyloops.evochia.core.commons.exceptions.ValueCannotBeNullOrEmptyException;
import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.persistance.table.TableInfoRepository;
import com.multipartyloops.evochia.persistance.table.grouping.TableGroupingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.multipartyloops.evochia.core.commons.Preconditions.throwWhenNullOrEmpty;

@Service
public class TableService {

    private final TableGroupingRepository<TableGroupingDto> tableGroupingRepository;
    private final TableInfoRepository<TableInfoDto> tableInfoRepository;
    private final TransactionTemplate transactionTemplate;

    public TableService(TableGroupingRepository<TableGroupingDto> tableGroupingRepository, TableInfoRepository<TableInfoDto> tableInfoRepository, TransactionTemplate transactionTemplate) {
        this.tableGroupingRepository = tableGroupingRepository;
        this.tableInfoRepository = tableInfoRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public TableInfoDto addTable(String tableGroupName, String tableAlias, boolean enabled) {
        preconditions(tableGroupName, tableAlias);
        transactionTemplate.executeWithoutResult(status -> {
            tableGroupingRepository.getTableGroupByName(tableGroupName)
                                   .ifPresentOrElse(
                                           tableGrouping -> tableInfoRepository.insertTable(
                                                   new TableInfoDto(UUID.randomUUID().toString(), tableAlias, tableGrouping.getGroupId(), enabled)
                                           ),
                                           createTableWithNewGrouping(tableGroupName, tableAlias, enabled));
        });
        return tableInfoRepository.getTableByAlias(tableAlias).orElseGet(TableInfoDto::new);
    }

    public void removeTable(String tableAlias) {
        throwWhenNullOrEmpty(tableAlias, new ValueCannotBeNullOrEmptyException("Table Alias cannot be null or empty"));
        tableInfoRepository.getTableByAlias(tableAlias)
                           .ifPresent(table -> tableInfoRepository.deleteTable(table.getTableId()));
    }

    public void disableTable(String tableAlias) {
        throwWhenNullOrEmpty(tableAlias, new ValueCannotBeNullOrEmptyException("Table Alias cannot be null or empty"));
        tableInfoRepository.getTableByAlias(tableAlias)
                           .ifPresent(table -> tableInfoRepository.disableTable(table.getTableId()));

    }

    public void enableTable(String tableAlias) {
        throwWhenNullOrEmpty(tableAlias, new ValueCannotBeNullOrEmptyException("Table Alias cannot be null or empty"));
        tableInfoRepository.getTableByAlias(tableAlias)
                           .ifPresent(table -> tableInfoRepository.enableTable(table.getTableId()));

    }

    public void enableTableGroup(String tableGroupId) {
        throwWhenNullOrEmpty(tableGroupId, new ValueCannotBeNullOrEmptyException("Table Group Id cannot be null or empty"));
        tableInfoRepository.getAllTables()
                           .stream()
                           .filter(table -> tableGroupId.equals(table.getGroupId()))
                           .forEach(table -> tableInfoRepository.enableTable(table.getTableId()));
    }

    public void disableTableGroup(String tableGroupId) {
        throwWhenNullOrEmpty(tableGroupId, new ValueCannotBeNullOrEmptyException("Table Group Id cannot be null or empty"));
        tableInfoRepository.getAllTables()
                           .stream()
                           .filter(table -> tableGroupId.equals(table.getGroupId()))
                           .forEach(table -> tableInfoRepository.disableTable(table.getTableId()));
    }

    private Runnable createTableWithNewGrouping(String groupName, String tableAlias, boolean enabled) {
        return () -> {
            final var tableGrouping = new TableGroupingDto(UUID.randomUUID().toString(), groupName, true);
            tableGroupingRepository.insertTableGroup(tableGrouping);
            tableInfoRepository.insertTable(
                    new TableInfoDto(UUID.randomUUID().toString(),
                                     tableAlias, tableGrouping.getGroupId(), enabled)
            );
        };
    }

    private void preconditions(String tableGroupName, String tableAlias) {
        throwWhenNullOrEmpty(tableGroupName, new ValueCannotBeNullOrEmptyException("Table Group Name cannot be null or empty"));
        throwWhenNullOrEmpty(tableAlias, new ValueCannotBeNullOrEmptyException("Table Alias cannot be null or empty"));
    }
}
