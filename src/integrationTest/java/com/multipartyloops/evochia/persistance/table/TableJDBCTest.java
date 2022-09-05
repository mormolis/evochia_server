package com.multipartyloops.evochia.persistance.table;

import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

public abstract class TableJDBCTest extends JDBCTest {
    protected JdbcTemplate jdbcTemplate = new JdbcTemplate(testDbDataSource);
    protected UuidPersistenceTransformer uuidPersistenceTransformer = new UuidPersistenceTransformer();

    protected TableGroupingDto insertAGroup(String name, boolean enabled) {
        TableGroupingDto tableGroupingDto = new TableGroupingDto(UUID.randomUUID().toString(), name, enabled);
        String sql = "INSERT INTO table_grouping (group_id, group_name, enabled) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                            uuidPersistenceTransformer.fromString(tableGroupingDto.getGroupId()),
                            tableGroupingDto.getGroupName(),
                            tableGroupingDto.getEnabled());
        return tableGroupingDto;
    }

    protected TableInfoDto insertATable(String alias, String groupId, boolean enabled) {
        TableInfoDto tableInfoDto = new TableInfoDto(UUID.randomUUID().toString(), alias, groupId, enabled);

        String sql = "INSERT INTO table_info (table_id, table_alias, group_id, enabled) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                            uuidPersistenceTransformer.fromString(tableInfoDto.getTableId()),
                            tableInfoDto.getTableAlias(),
                            uuidPersistenceTransformer.fromString(tableInfoDto.getGroupId()),
                            tableInfoDto.getEnabled());
        return  tableInfoDto;
    }

}
