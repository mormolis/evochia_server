package com.multipartyloops.evochia.persistance.terminal;

import com.multipartyloops.evochia.core.terminal.dto.TerminalDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TerminalsJDBCRepositoryTest extends JDBCTest {

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    UuidPersistenceTransformer uuidPersistenceTransformer = new UuidPersistenceTransformer();
    TerminalsJDBCRepository terminalsJDBCRepository;

    @BeforeEach
    void setup() {
        terminalsJDBCRepository = new TerminalsJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);
    }

    @Test
    void addAndRetrieveTerminals() {
        TerminalDto terminalDto = new TerminalDto(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        terminalsJDBCRepository.addTerminal(terminalDto);

        assertThat(terminalsJDBCRepository.getAllTerminals()).contains(terminalDto);
    }

    @Test
    void getTerminalById() {
        TerminalDto terminalDto = new TerminalDto(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        terminalsJDBCRepository.addTerminal(terminalDto);

        assertThat(terminalsJDBCRepository.getTerminalById(terminalDto.getTerminalId())).isEqualTo(Optional.of(terminalDto));
    }

    @Test
    void getTerminalByIdReturnsAnEmptyOptionalWhenRowIsNotPresent() {

        assertThat(terminalsJDBCRepository.getTerminalById(UUID.randomUUID().toString())).isEqualTo(Optional.empty());
    }

    @Test
    void aPersistedTerminalCanBeDeleted() {
        TerminalDto terminalDto = new TerminalDto(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        terminalsJDBCRepository.addTerminal(terminalDto);

        terminalsJDBCRepository.deleteTerminal(terminalDto.getTerminalId());

        assertThat(terminalsJDBCRepository.getTerminalById(terminalDto.getTerminalId())).isEqualTo(Optional.empty());
    }

    @Test
    void aPersistedTerminalCanBeUpdated() {
        String terminalId = UUID.randomUUID().toString();
        TerminalDto persisted = new TerminalDto(terminalId, UUID.randomUUID().toString());
        TerminalDto toUpdate = new TerminalDto(terminalId, UUID.randomUUID().toString());
        terminalsJDBCRepository.addTerminal(persisted);

        terminalsJDBCRepository.updateTerminal(toUpdate);

        assertThat(terminalsJDBCRepository.getTerminalById(terminalId)).isEqualTo(Optional.of(toUpdate));
    }
}