package com.multipartyloops.evochia.core.identity.commons;

import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
