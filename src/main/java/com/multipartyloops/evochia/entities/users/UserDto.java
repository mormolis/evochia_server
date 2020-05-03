package com.multipartyloops.evochia.entities.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonProperty
    private String userId;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private List<Roles> roles;

    @JsonProperty
    private String name;

    @JsonProperty
    private String telephone;
}
