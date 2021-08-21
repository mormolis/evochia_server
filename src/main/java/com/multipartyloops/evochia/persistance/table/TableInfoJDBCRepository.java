package com.multipartyloops.evochia.persistance.table;

import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.table.TableInfoSQLStatements.DELETE_TABLE_BY_ID;
import static com.multipartyloops.evochia.persistance.table.TableInfoSQLStatements.GET_ALL_TABLES;
import static com.multipartyloops.evochia.persistance.table.TableInfoSQLStatements.INSERT_TABLE;
import static com.multipartyloops.evochia.persistance.table.TableInfoSQLStatements.SELECT_TABLE_BY_ALIAS;
import static com.multipartyloops.evochia.persistance.table.TableInfoSQLStatements.SELECT_TABLE_BY_ID;

@Repository
public class TableInfoJDBCRepository implements TableInfoRepository<TableInfoDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public TableInfoJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public void insertTable(TableInfoDto table) {
        jdbcTemplate.update(INSERT_TABLE,
                            uuidPersistenceTransformer.fromString(table.getTableId()),
                            table.getTableAlias(),
                            uuidPersistenceTransformer.fromString(table.getGroupId()),
                            table.getEnabled()
        );
    }

    @Override
    public void deleteTable(String tableId) {
        Object binaryTableId = uuidPersistenceTransformer.fromString(tableId);
        jdbcTemplate.update(DELETE_TABLE_BY_ID,
                            binaryTableId);
    }

    @Override
    public List<TableInfoDto> getAllTables() {
        return jdbcTemplate.query(GET_ALL_TABLES,
                                  this::parseTable);
    }

    @Override
    public Optional<TableInfoDto> getTableById(String tableId) {
        List<TableInfoDto> list = jdbcTemplate.query(SELECT_TABLE_BY_ID,
                                                     this::parseTable,
                                                     uuidPersistenceTransformer.fromString(tableId)
        );

        if (list.size() == 1) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<TableInfoDto> getTableByAlias(String tableAlias) {
        List<TableInfoDto> list = jdbcTemplate.query(SELECT_TABLE_BY_ALIAS,
                                                     this::parseTable,
                                                     tableAlias
        );

        if (list.size() == 1) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    private TableInfoDto parseTable(ResultSet resultSet, int _i) throws SQLException {
        return new TableInfoDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("table_id")),
                resultSet.getString("table_alias"),
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("group_id")),
                resultSet.getBoolean("enabled")
        );
    }
}
