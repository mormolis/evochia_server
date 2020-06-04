package com.multipartyloops.evochia.persistance.terminal;

import java.util.List;
import java.util.Optional;

public interface TerminalsRepository<T> {

    void addTerminal(T terminal);

    void deleteTerminal(String terminalId);

    void updateTerminal(T terminal);

    List<T> getAllTerminals();

    Optional<T> getTerminalById(String terminalId);
}
