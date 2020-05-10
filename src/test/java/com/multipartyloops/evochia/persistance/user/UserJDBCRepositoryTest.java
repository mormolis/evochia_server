package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import com.multipartyloops.evochia.core.identity.user.entities.UserDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserJDBCRepositoryTest extends JDBCTest {

    private UserJDBCRepository userJDBCRepository;

    @BeforeEach
    void setup() {
        userJDBCRepository = new UserJDBCRepository(new JdbcTemplate(dataSource), new UuidPersistenceTransformer());
    }

    @Test
    void returnsUserById() {
        List<Roles> roles = List.of(Roles.ADMIN, Roles.STAFF);
        String userId = UUID.randomUUID().toString();
        UserDto user1 = new UserDto(userId, "mormolis_1", "a_password", roles, "Giorgis", "07491000000");

        userJDBCRepository.storeUser(user1);

        assertThat(userJDBCRepository.getUserById(userId)).isEqualTo(user1);
    }

    @Test
    void returnsUserByUsername() {
        List<Roles> roles = List.of(Roles.ADMIN, Roles.STAFF);
        UserDto user1 = new UserDto(UUID.randomUUID().toString(), "mormolis_3", "a_password", roles, "Giorgis", "07491000000");

        userJDBCRepository.storeUser(user1);

        assertThat(userJDBCRepository.getUserByUsername("mormolis_3")).isEqualTo(user1);
    }

    @Test
    void userNameMustBeUnique() {
        List<Roles> roles = List.of(Roles.ADMIN, Roles.STAFF);
        UserDto user = new UserDto(UUID.randomUUID().toString(), "mormolis_5", "a_password", roles, "Giorgis", "07491000000");
        UserDto userWithTheSameUsername = new UserDto(UUID.randomUUID().toString(), "mormolis_5", "a_password", roles, "Giorgis", "07491000000");

        userJDBCRepository.storeUser(user);
        assertThrows(DuplicateKeyException.class, () ->
                userJDBCRepository.storeUser(userWithTheSameUsername)
        );
    }

    @Test
    void userIdMustBeUnique() {
        List<Roles> roles = List.of(Roles.ADMIN, Roles.STAFF);
        String userId = UUID.randomUUID().toString();
        UserDto user = new UserDto(userId, "mormolis_6", "a_password", roles, "Giorgis", "07491000000");
        UserDto userWithTheSameUsername = new UserDto(userId, "another_mormolis", "a_password", roles, "Giorgis", "07491000000");

        userJDBCRepository.storeUser(user);
        assertThrows(DuplicateKeyException.class, () ->
                userJDBCRepository.storeUser(userWithTheSameUsername)
        );
    }

    @Test
    void returnsAListOfAllUsers() {
        List<UserDto> expected = new ArrayList<>();
        setupCleanDatabase();
        populateDbWithTestData(expected);

        List<UserDto> allUsers = userJDBCRepository.getAllUsers();

        assertThat(allUsers).contains(expected.toArray(new UserDto[0]));
    }

    @Test
    void getsUsersByRole(){
        List<UserDto> expected = new ArrayList<>();
        setupCleanDatabase();
        populateDbWithTestData(expected);

        List<UserDto> allUsers = userJDBCRepository.getAllUsersByRole(Roles.STAFF);

        assertThat(allUsers).contains(expected.toArray(new UserDto[0]));
    }

    private void populateDbWithTestData(List<UserDto> usersToExpect) {

        List<Roles> roles = List.of(Roles.ADMIN, Roles.FINANCE, Roles.STAFF);
        IntStream.range(10,13).forEach(i ->{
            UserDto userToStore = new UserDto(UUID.randomUUID().toString(), "mormolis_" + i, "a_password", roles, "Giorgis", "07491000000");
            userJDBCRepository.storeUser(userToStore);
            usersToExpect.add(userToStore);
        });

        List<Roles> roles2 = List.of(Roles.STAFF);
        IntStream.range(13,16).forEach(i ->{
            UserDto userToStore = new UserDto(UUID.randomUUID().toString(), "mormolis_" + i, "a_password", roles2, "Giorgis", "07491000000");
            userJDBCRepository.storeUser(userToStore);
            usersToExpect.add(userToStore);
        });
    }
}