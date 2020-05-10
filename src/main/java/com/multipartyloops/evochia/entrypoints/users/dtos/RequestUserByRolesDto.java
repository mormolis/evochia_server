package com.multipartyloops.evochia.entrypoints.users.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserByRolesDto {

    @JsonProperty
    private List<Roles> roles;
}
