package com.multipartyloops.evochia.persistance.table;

import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.persistance.table.grouping.TableGroupingJDBCRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TableGroupingJDBCRepositoryTest extends TableJDBCTest {

    private TableGroupingJDBCRepository tableGroupingJDBCRepository;

    @BeforeEach
    void setUp() {
        tableGroupingJDBCRepository = new TableGroupingJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);
    }

    @Test
    void retrievesATableGrouping() {
        final var tableGroupingDto = super.insertAGroup(UUID.randomUUID().toString(), true);

        final var tableGroupById = tableGroupingJDBCRepository.getTableGroupById(tableGroupingDto.getGroupId());

        assertThat(tableGroupById.get()).isEqualTo(tableGroupingDto);
    }

    @Test
    void retrievesATableGroupingByName() {
        final var tableGroupingDto = super.insertAGroup(UUID.randomUUID().toString(), true);

        final var tableGroupById = tableGroupingJDBCRepository.getTableGroupByName(tableGroupingDto.getGroupName());

        assertThat(tableGroupById.get()).isEqualTo(tableGroupingDto);
    }

    @Test
    void insertsATableGrouping() {
        TableGroupingDto tableGroupingDto = new TableGroupingDto(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true);

        tableGroupingJDBCRepository.insertTableGroup(tableGroupingDto);

        assertThat(tableGroupingJDBCRepository.getTableGroupById(tableGroupingDto.getGroupId()).get()).isEqualTo(tableGroupingDto);
    }

    @Test
    void deletesATableGrouping() {
        final var tableGroupingDto = super.insertAGroup(UUID.randomUUID().toString(), true);

        tableGroupingJDBCRepository.deleteTableGroup(tableGroupingDto.getGroupId());

        assertThat(tableGroupingJDBCRepository.getTableGroupById(tableGroupingDto.getGroupId())).isEmpty();
    }

    @Test
    void getsAllTableGroups() {
        setupCleanDatabase();
        final var aGroup = super.insertAGroup(UUID.randomUUID().toString(), true);
        final var anotherGroup = super.insertAGroup(UUID.randomUUID().toString(), false);

        final var allTableGroups = tableGroupingJDBCRepository.getAllTableGroups();

        assertThat(allTableGroups).asList().containsExactlyInAnyOrder(aGroup, anotherGroup);
    }

}
