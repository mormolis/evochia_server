package com.multipartyloops.evochia.persistance.table;

import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TableInfoJDBCRepositoryTest extends TableJDBCTest {

    private TableInfoJDBCRepository tableInfoJDBCRepository;

    @BeforeEach
    void setUp() {
        tableInfoJDBCRepository = new TableInfoJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);
    }

    @Test
    void retirevesATable() {
        final var tableGroup = super.insertAGroup("aGroup", true);
        final var tableInfoDto = insertATable("an-alias", tableGroup.getGroupId(), true);

        final var tableById = tableInfoJDBCRepository.getTableById(tableInfoDto.getTableId());

        assertThat(tableById.get()).isEqualTo(tableInfoDto);
    }

    @Test
    void retrievesATableByAlias() {
        final var tableGroup = super.insertAGroup("aName", true);
        final var tableInfoDto = insertATable("an-alias", tableGroup.getGroupId(), true);

        final var tableInfoById = tableInfoJDBCRepository.getTableByAlias("an-alias");

        assertThat(tableInfoById.get()).isEqualTo(tableInfoDto);
    }

    @Test
    void insertATableInfo() {
        final var tableGroup = super.insertAGroup("aName", true);
        final var table = new TableInfoDto(UUID.randomUUID().toString(), "my-table", tableGroup.getGroupId(), true);

        tableInfoJDBCRepository.insertTable(table);

        assertThat(tableInfoJDBCRepository.getTableById(table.getTableId()).get()).isEqualTo(table);
    }

    @Test
    void deletesATableInfo() {
        final var tableGroup = super.insertAGroup("aName", true);
        final var tableInfoDto = insertATable("an-alias", tableGroup.getGroupId(), true);

        tableInfoJDBCRepository.deleteTable(tableInfoDto.getTableId());

        assertThat(tableInfoJDBCRepository.getTableById(tableInfoDto.getTableId())).isEmpty();
    }

    @Test
    void getsAllTableInfo() {
        final var tableGroup = super.insertAGroup("aName", true);
        final var table = insertATable("an-alias", tableGroup.getGroupId(), true);
        final var anotherTable = insertATable("another-alias", tableGroup.getGroupId(), false);

        final var allTables = tableInfoJDBCRepository.getAllTables();

        assertThat(allTables).asList().containsExactlyInAnyOrder(table, anotherTable);
    }

    @Test
    void updatesTableStatusFromEnabledToDisabled(){
        final var tableGroup = super.insertAGroup("aName", true);
        final var table = insertATable("an-alias", tableGroup.getGroupId(), true);

        tableInfoJDBCRepository.disableTable(table.getTableId());

        assertThat(tableInfoJDBCRepository.getTableById(table.getTableId()).get().getEnabled()).isFalse();
    }

    @Test
    void updatesTableStatusFromDisabledToEnabled(){
        final var tableGroup = super.insertAGroup("aName", true);
        final var table = insertATable("an-alias", tableGroup.getGroupId(), false);

        tableInfoJDBCRepository.enableTable(table.getTableId());

        assertThat(tableInfoJDBCRepository.getTableById(table.getTableId()).get().getEnabled()).isTrue();
    }
}
