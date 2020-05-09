package com.multipartyloops.evochia.entities.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientCredentialsDto {

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String secret;

    @JsonProperty
    private String device;
}
