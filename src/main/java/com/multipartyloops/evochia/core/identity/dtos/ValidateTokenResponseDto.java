package com.multipartyloops.evochia.core.identity.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidateTokenResponseDto {

    @JsonProperty
    private boolean valid;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String roles;

}
