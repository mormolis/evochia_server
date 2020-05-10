package com.multipartyloops.evochia.entrypoints.identity.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenIssuanceRequestBody {

    @JsonProperty
    String clientId;

    @JsonProperty
    String secret;

    @JsonProperty
    String username;

    @JsonProperty
    String password;
}
