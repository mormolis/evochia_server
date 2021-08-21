package com.multipartyloops.evochia.core.terminal;

import com.multipartyloops.evochia.core.terminal.dto.TerminalDto;
import com.multipartyloops.evochia.core.terminal.exceptions.TerminalNotFoundException;
import com.multipartyloops.evochia.core.commons.exceptions.ValueCannotBeNullOrEmptyException;
import com.multipartyloops.evochia.persistance.terminal.TerminalsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.multipartyloops.evochia.core.commons.Preconditions.throwWhenNullOrEmpty;
import static com.multipartyloops.evochia.core.commons.UUIDFormatChecker.confirmOrThrow;

@Service
public class TerminalService {

    private TerminalsRepository<TerminalDto> terminalsRepository;

    public TerminalService(TerminalsRepository<TerminalDto> terminalsRepository) {
        this.terminalsRepository = terminalsRepository;
    }

    public TerminalDto addTerminal(String terminalName) {
        throwWhenNullOrEmpty(terminalName, new ValueCannotBeNullOrEmptyException("Terminal name cannot be null or empty"));
        TerminalDto terminalDto = new TerminalDto(UUID.randomUUID().toString(),
                terminalName);

        terminalsRepository.addTerminal(terminalDto);
        return terminalDto;
    }

    public void deleteTerminalById(String terminalId) {
        throwWhenNullOrEmpty(terminalId, new ValueCannotBeNullOrEmptyException("Terminal id cannot be null or empty"));

        try {
            confirmOrThrow(terminalId, new IllegalArgumentException());
            terminalsRepository.deleteTerminal(terminalId);
        } catch (IllegalArgumentException e) {
            // no need to do anything
        }
    }

    public void updateTerminal(TerminalDto terminalDto) {
        throwWhenNullOrEmpty(terminalDto.getName(), new ValueCannotBeNullOrEmptyException("Terminal name cannot be null or empty"));
        throwWhenNullOrEmpty(terminalDto.getTerminalId(), new ValueCannotBeNullOrEmptyException("Terminal id cannot be null or empty"));

        try {
            confirmOrThrow(terminalDto.getTerminalId(), new IllegalArgumentException());
            terminalsRepository.updateTerminal(terminalDto);
        } catch (IllegalArgumentException e) {
            //no need to do anything
        }
    }

    public List<TerminalDto> getTerminals() {
        return terminalsRepository.getAllTerminals();
    }

    public TerminalDto getTerminalById(String terminalId) {
        throwWhenNullOrEmpty(terminalId, new ValueCannotBeNullOrEmptyException("Terminal id cannot be null or empty"));
        confirmOrThrow(terminalId, new TerminalNotFoundException("Terminal not found"));

        return terminalsRepository.getTerminalById(terminalId)
                .orElseThrow(() -> new TerminalNotFoundException("Terminal not found"));
    }

}
