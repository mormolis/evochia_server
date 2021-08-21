package com.multipartyloops.evochia.persistance.table.grouping;

import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.table.grouping.TableGroupingSQLStatements.DELETE_TABLE_GROUPING;
import static com.multipartyloops.evochia.persistance.table.grouping.TableGroupingSQLStatements.SELECT_ALL_TABLE_GROUPS;
import static com.multipartyloops.evochia.persistance.table.grouping.TableGroupingSQLStatements.SELECT_GROUP_NAME_BY_ID;
import static com.multipartyloops.evochia.persistance.table.grouping.TableGroupingSQLStatements.SELECT_TABLE_GROUP_BY_ID;
import static com.multipartyloops.evochia.persistance.table.grouping.TableGroupingSQLStatements.TABLE_GROUPING_INSERTION;

@Repository
public class TableGroupingJDBCRepository implements TableGroupingRepository<TableGroupingDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public TableGroupingJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public void insertTableGroup(TableGroupingDto group) {
        jdbcTemplate.update(TABLE_GROUPING_INSERTION,
                            uuidPersistenceTransformer.fromString(group.getGroupId()),
                            group.getGroupName(),
                            group.getEnabled()
        );
    }

    @Override
    public void deleteTableGroup(String groupId) {
        Object binaryTableGroupId = uuidPersistenceTransformer.fromString(groupId);
        jdbcTemplate.update(DELETE_TABLE_GROUPING,
                            binaryTableGroupId);
    }

    @Override
    public List<TableGroupingDto> getAllTableGroups() {
       return jdbcTemplate.query(SELECT_ALL_TABLE_GROUPS,
                                 this::parseTableGrouping);
    }

    @Override
    public Optional<TableGroupingDto> getTableGroupById(String groupId) {
        Object groupIdInBytes = uuidPersistenceTransformer.fromString(groupId);
        List<TableGroupingDto> list = jdbcTemplate.query(SELECT_TABLE_GROUP_BY_ID,
                                                         this::parseTableGrouping,
                                                         groupIdInBytes
        );

        if(list.size() == 1) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<TableGroupingDto> getTableGroupByName(String groupName) {
        List<TableGroupingDto> list = jdbcTemplate.query(SELECT_GROUP_NAME_BY_ID,
                                                         this::parseTableGrouping,
                                                         groupName
        );

        if(list.size() == 1) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    private TableGroupingDto parseTableGrouping(ResultSet resultSet, int _i) throws SQLException {
        return new TableGroupingDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("group_id")),
                resultSet.getString("group_name"),
                resultSet.getBoolean("enabled")
        );
    }
}
