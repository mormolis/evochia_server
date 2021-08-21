package com.multipartyloops.evochia.core.identity.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthenticationDto {

    private String userId;
    private List<Roles> roles;
}
