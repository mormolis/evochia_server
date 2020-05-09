package com.multipartyloops.evochia.core.identity;

import com.multipartyloops.evochia.entities.user.Roles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RolesConverter {


    public List<Roles> fromString(String givenRoles) {
        return Arrays.stream(givenRoles.split(" "))
                .map(Roles::valueOf)
                .collect(Collectors.toList());
    }

    public String fromList(List<Roles> rolesList) {
        return rolesList
                .stream()
                .collect(
                        StringBuilder::new,
                        (sb, role) -> sb.append(role).append(" "),
                        StringBuilder::append
                ).toString()
                .trim();
    }
}
