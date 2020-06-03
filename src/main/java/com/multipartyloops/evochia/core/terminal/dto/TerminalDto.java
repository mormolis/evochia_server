package com.multipartyloops.evochia.core.terminal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminalDto {

    @JsonProperty
    private String terminalId;

    @JsonProperty
    private String name;
}
