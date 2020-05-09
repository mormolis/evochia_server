package com.multipartyloops.evochia.core.identity.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenDto {

    @JsonProperty
    private String token;

    @JsonProperty
    private LocalDateTime tokenExpiry;

    @JsonProperty
    private String refreshToken;

    @JsonProperty
    private LocalDateTime refreshTokenExpiry;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String roles;
}

