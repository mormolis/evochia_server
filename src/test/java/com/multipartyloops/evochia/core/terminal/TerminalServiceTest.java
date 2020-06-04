package com.multipartyloops.evochia.core.terminal;

import com.multipartyloops.evochia.core.terminal.dto.TerminalDto;
import com.multipartyloops.evochia.core.terminal.exceptions.TerminalNotFoundException;
import com.multipartyloops.evochia.persistance.terminal.TerminalsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TerminalServiceTest {

    @Mock
    private TerminalsRepository<TerminalDto> terminalsRepositoryMock;

    private TerminalService terminalService;

    @BeforeEach
    void setup(){
        terminalService = new TerminalService(terminalsRepositoryMock);
    }
    @Test
    void canPersistATerminal(){
        String terminalName = "main bar";

        TerminalDto terminalDto = terminalService.addTerminal(terminalName);

        then(terminalsRepositoryMock).should().addTerminal(terminalDto);
        assertThat(terminalDto.getName()).isEqualTo(terminalName);
    }

    @Test
    void canDeleteATerminalById(){
        String terminalId = UUID.randomUUID().toString();

        terminalService.deleteTerminalById(terminalId);

        then(terminalsRepositoryMock).should().deleteTerminal(terminalId);
    }

    @Test
    void canUpdateATerminal(){
        TerminalDto terminalDto = new TerminalDto(UUID.randomUUID().toString(), "main bar");

        terminalService.updateTerminal(terminalDto);

        then(terminalsRepositoryMock).should().updateTerminal(terminalDto);
    }

    @Test
    void canGetAllPersistedTerminals(){
        List<TerminalDto> terminalDtos = List.of(new TerminalDto("1", "mainbar"), new TerminalDto("2", "kitchen"));
        when(terminalsRepositoryMock.getAllTerminals()).thenReturn(terminalDtos);

        List<TerminalDto> terminals = terminalService.getTerminals();

        assertThat(terminals).isEqualTo(terminalDtos);
    }

    @Test
    void terminalCanBeRetrievedById(){
        String terminalId = UUID.randomUUID().toString();
        TerminalDto mainBar = new TerminalDto(terminalId, "main bar");
        when(terminalsRepositoryMock.getTerminalById(terminalId)).thenReturn(Optional.of(mainBar));

        TerminalDto retrievedTerminal = terminalService.getTerminalById(terminalId);

        assertThat(retrievedTerminal).isEqualTo(mainBar);
    }

    @Test
    void tryingToRetrieveATerminalThatDoesNotExistWillThrowAnException(){
        String terminalId = UUID.randomUUID().toString();
        when(terminalsRepositoryMock.getTerminalById(terminalId)).thenReturn(Optional.empty());

        assertThatThrownBy(()->terminalService.getTerminalById(terminalId))
                .hasMessage("Terminal not found")
                .isInstanceOf(TerminalNotFoundException.class);

    }


}