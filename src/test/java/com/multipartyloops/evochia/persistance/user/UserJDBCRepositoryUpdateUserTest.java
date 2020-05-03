package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.entities.users.Roles;
import com.multipartyloops.evochia.entities.users.UserDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJDBCRepositoryUpdateUserTest extends JDBCTest{

    @Test
    void updateAUserThatDoesNotExist(){

    }

    @Test
    void updatesAUserWithNoRolesAssigned(){
        List<Roles> roles = new ArrayList<>();// List.of(Roles.ADMIN, Roles.STAFF);
        String userId = UUID.randomUUID().toString();
        UserDto userBefore = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_password", roles, "Giorgis", "07491000000");
        UserDto userAfter = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_different_password", roles, "Giorgis", "07491000000");
        userJDBCRepository.storeUser(userBefore);

        userJDBCRepository.updateUser(userAfter);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(userAfter);
    }

    @Test
    void updatesAUserWithRolesAssigned_sc1(){
        List<Roles> rolesBefore =  List.of(Roles.STAFF);
        List<Roles> rolesAfter =  List.of(Roles.ADMIN, Roles.FINANCE);
        String userId = UUID.randomUUID().toString();
        UserDto userBefore = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_password", rolesBefore, "Giorgis", "07491000000");
        UserDto userAfter = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_different_password", rolesAfter, "Giorgis", "07491000000");
        userJDBCRepository.storeUser(userBefore);

        userJDBCRepository.updateUser(userAfter);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(userAfter);
    }

    @Test
    void updatesAUserWithRolesAssigned_sc2(){
        List<Roles> rolesBefore =  List.of();
        List<Roles> rolesAfter =  List.of(Roles.ADMIN, Roles.FINANCE);
        String userId = UUID.randomUUID().toString();
        UserDto userBefore = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_password", rolesBefore, "Giorgis", "07491000000");
        UserDto userAfter = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_different_password", rolesAfter, "Giorgis", "07491000000");
        userJDBCRepository.storeUser(userBefore);

        userJDBCRepository.updateUser(userAfter);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(userAfter);
    }

    @Test
    void updatesAUserWithRolesAssigned_sc3(){
        List<Roles> rolesBefore =  List.of(Roles.ADMIN);
        List<Roles> rolesAfter =  List.of(Roles.ADMIN, Roles.FINANCE);
        String userId = UUID.randomUUID().toString();
        UserDto userBefore = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_password", rolesBefore, "Giorgis", "07491000000");
        UserDto userAfter = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_different_password", rolesAfter, "Giorgis", "07491000000");
        userJDBCRepository.storeUser(userBefore);

        userJDBCRepository.updateUser(userAfter);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(userAfter);
    }

    @Test
    void updatesAUserWithRolesAssigned_sc4(){
        List<Roles> rolesBefore =  List.of(Roles.ADMIN);
        List<Roles> rolesAfter =  List.of(Roles.FINANCE);
        String userId = UUID.randomUUID().toString();
        UserDto userBefore = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_password", rolesBefore, "Giorgis", "07491000000");
        UserDto userAfter = new UserDto(userId, "mormolis_" + UUID.randomUUID(), "a_different_password", rolesAfter, "Giorgis", "07491000000");
        userJDBCRepository.storeUser(userBefore);

        userJDBCRepository.updateUser(userAfter);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(userAfter);
    }

}
