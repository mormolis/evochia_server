package com.multipartyloops.evochia.entrypoints.terminal;

import com.multipartyloops.evochia.core.terminal.TerminalService;
import com.multipartyloops.evochia.core.terminal.dto.TerminalDto;
import com.multipartyloops.evochia.entrypoints.terminal.dtos.TerminalIdDto;
import com.multipartyloops.evochia.entrypoints.terminal.dtos.TerminalNameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/terminals")
public class TerminalController {


    private TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TerminalDto>> getTerminals(@RequestHeader Map<String, String> headers) {

        List<TerminalDto> terminals = terminalService.getTerminals();
        return new ResponseEntity<>(terminals, HttpStatus.OK);
    }

    @RequestMapping(value = "/terminal/add", method = RequestMethod.POST)
    public ResponseEntity<TerminalDto> addTerminal(@RequestHeader Map<String, String> headers, @RequestBody TerminalNameDto body) {

        TerminalDto terminalDto = terminalService.addTerminal(body.getName());
        return new ResponseEntity<>(terminalDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/terminal/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteTerminal(@RequestHeader Map<String, String> headers, @RequestBody TerminalIdDto body) {

        terminalService.deleteTerminalById(body.getTerminalId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/terminal/update", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateTerminal(@RequestHeader Map<String, String> headers, @RequestBody TerminalDto body) {

        terminalService.updateTerminal(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/terminal/{terminal_id}", method = RequestMethod.GET)
    public ResponseEntity<TerminalDto> updateTerminal(@RequestHeader Map<String, String> headers, @PathVariable("terminal_id") String terminalId) {

        TerminalDto terminalById = terminalService.getTerminalById(terminalId);
        return new ResponseEntity<>(terminalById, HttpStatus.OK);
    }
}