package com.multipartyloops.evochia.core.identity;

import com.multipartyloops.evochia.core.user.entities.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RolesConverterTest {

    private RolesConverter rolesConverter;

    @BeforeEach
    void setup() {
        rolesConverter = new RolesConverter();
    }

    @Test
    void takesASpaceSeparatedStringAndMakesAListOfRoles() {
        String givenRoles = "ADMIN FINANCE STAFF";

        List<Roles> listOfRoles = rolesConverter.fromString(givenRoles);

        assertThat(listOfRoles).contains(Roles.ADMIN, Roles.STAFF, Roles.FINANCE);
    }

    @Test
    void rolesStringValuesCanOnlyContainValidRolesFromTheEnum() {
        String givenInvalidRoles = "THE BOSS IS PROCRASTINATOR";

        assertThatThrownBy(() -> rolesConverter.fromString(givenInvalidRoles))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertsAListOfRolesToStringOfRolesSeparatedBySpace() {
        List<Roles> rolesList = List.of(Roles.ADMIN, Roles.STAFF);

        String rolesString = rolesConverter.fromList(rolesList);

        assertThat(rolesString).isEqualTo("ADMIN STAFF");
    }
}