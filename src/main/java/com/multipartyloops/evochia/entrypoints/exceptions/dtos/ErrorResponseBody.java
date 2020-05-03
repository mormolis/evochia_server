package com.multipartyloops.evochia.entrypoints.exceptions.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseBody {
    @JsonProperty
    private String message;
}
