package com.multipartyloops.evochia.persistance.terminal;

import com.multipartyloops.evochia.core.terminal.dto.TerminalDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.terminal.TerminalsSQLStatements.*;

@Repository
public class TerminalsJDBCRepository implements TerminalsRepository<TerminalDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public TerminalsJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public void addTerminal(TerminalDto terminal) {

        jdbcTemplate.update(TERMINALS_INSERTION,
                uuidPersistenceTransformer.fromString(terminal.getTerminalId()),
                terminal.getName()
        );
    }

    @Override
    public void deleteTerminal(String terminalId) {
        Object binaryTerminalId = uuidPersistenceTransformer.fromString(terminalId);
        jdbcTemplate.update(TERMINALS_DELETE_BY_ID,
                binaryTerminalId
        );
    }

    @Override
    public void updateTerminal(TerminalDto terminal) {
        jdbcTemplate.update(TERMINALS_UPDATE,
                terminal.getName(),
                uuidPersistenceTransformer.fromString(terminal.getTerminalId())
        );
    }

    @Override
    public List<TerminalDto> getAllTerminals() {
        return jdbcTemplate.query(TERMINALS_SELECT_ALL, this::parseTerminal);
    }

    @Override
    public Optional<TerminalDto> getTerminalById(String terminalId) {

        Object binaryTerminalId = uuidPersistenceTransformer.fromString(terminalId);
        List<TerminalDto> query = jdbcTemplate.query(TERMINALS_SELECT_BY_ID,
                this::parseTerminal,
                binaryTerminalId);
        if (query.size() == 1) {
            return Optional.of(query.get(0));
        }
        return Optional.empty();
    }

    private TerminalDto parseTerminal(ResultSet resultSet, int i) throws SQLException {
        return new TerminalDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("terminal_id")),
                resultSet.getString("name")
        );
    }
}
