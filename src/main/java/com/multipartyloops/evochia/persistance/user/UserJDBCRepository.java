package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.entities.user.Roles;
import com.multipartyloops.evochia.entities.user.UserDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.multipartyloops.evochia.persistance.user.UserSQLStatements.*;

@Repository
public class UserJDBCRepository implements UserRepository<UserDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public UserJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public UserDto getUserById(String id) {
        Object binaryUserId = uuidPersistenceTransformer.fromString(id);
        List<Roles> roles = jdbcTemplate.query(ROLES_SELECT_BY_USER_ID, this::roleFromResultSet, binaryUserId);
        UserDto user = getUserByIdWithoutRole(binaryUserId);
        user.setRoles(roles);
        return user;
    }

    @Override
    public UserDto getUserByUsername(String username) {

        UserDto user = getUserByUsernameWithoutRole(username);
        Object userIdInBytes = uuidPersistenceTransformer.fromString(user.getUserId());
        List<Roles> roles = getRolesByUserId(userIdInBytes);
        user.setRoles(roles);
        return user;
    }

    @Override
    public void storeUser(UserDto user) {

        Object binaryUserId = uuidPersistenceTransformer.fromString(user.getUserId());

        jdbcTemplate.update(USERS_INSERTION, binaryUserId, user.getUsername(), user.getPassword(), user.getName(), user.getTelephone());

        for (Roles role : user.getRoles()) {
            jdbcTemplate.update(ROLES_INSERTION, uuidPersistenceTransformer.fromString(UUID.randomUUID().toString()), binaryUserId, role.name());
        }
    }

    @Override
    public void updateUser(UserDto user) {
        Object binaryUserId = uuidPersistenceTransformer.fromString(user.getUserId());
        jdbcTemplate.update(USERS_UPDATE, user.getUsername(), user.getPassword(), user.getName(), user.getTelephone(), binaryUserId);
        List<Roles> existingRoles = getRolesByUserId(binaryUserId);
        List<Roles> newRoles = user.getRoles();

        updateRoles(binaryUserId, existingRoles, newRoles);
    }

    @Override
    public List<UserDto> getAllUsers() {

        List<UserDto> allUsers = jdbcTemplate.query(USERS_SELECT_STAR, this::parseUser);
        populateUserWithTheirRoles(allUsers);
        return allUsers;
    }


    @Override
    public List<UserDto> getAllUsersByRole(Roles role) {

        List<byte[]> userIds = jdbcTemplate.query(ROLES_SELECT_USER_ID_BY_ROLE, this::getUserId, role.name());
        List<UserDto> users = new ArrayList<>();
        // think about returning the users without the roles to make it faster
        userIds.forEach(userId -> {
            UserDto userById = getUserById(uuidPersistenceTransformer.getUUIDFromBytes(userId));
            users.add(userById);
        });

        return users;
    }


    private UserDto getUserByIdWithoutRole(Object binaryUserId) {
        List<UserDto> queryResults = jdbcTemplate.query(USERS_SELECT_BY_ID, this::parseUser, binaryUserId);
        if (queryResults.size() == 1) {
            return queryResults.get(0);
        }
        throw new RowNotFoundException("User not found");
    }

    private UserDto getUserByUsernameWithoutRole(String username) {
        List<UserDto> queryResults = jdbcTemplate.query(USERS_SELECT_BY_USERNAME, this::parseUser, username);
        if (queryResults.size() == 1) {
            return queryResults.get(0);
        }
        throw new RowNotFoundException("User not found");
    }

    private UserDto parseUser(ResultSet resultSet, int _rowNumber) throws SQLException {
        UserDto user = new UserDto();
        user.setUserId(uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("user_id")));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setName(resultSet.getString("name"));
        user.setTelephone(resultSet.getString("telephone"));
        return user;
    }

    private List<Roles> getRolesByUserId(Object userIdInBytes) {
        return jdbcTemplate.query(
                ROLES_SELECT_BY_USER_ID,
                (resultSet, _rowNum) -> Roles.valueOf(resultSet.getString("role")),
                userIdInBytes
        );
    }

    private Roles roleFromResultSet(ResultSet resultSet, int _rowNum) throws SQLException {
        return Roles.valueOf(resultSet.getString("role"));
    }

    private void updateRoles(Object binaryUserId, List<Roles> existingRoles, List<Roles> newRoles) {
        existingRoles.forEach(existingRole -> {
            if (!newRoles.contains(existingRole)) {
                jdbcTemplate.update(ROLES_DELETE_BY_USER_ID_AND_ROLE, binaryUserId, existingRole.name());
            }
        });

        newRoles.forEach(newRole -> {
            if (!existingRoles.contains(newRole)) {
                jdbcTemplate.update(ROLES_INSERTION, uuidPersistenceTransformer.fromString(UUID.randomUUID().toString()), binaryUserId, newRole.name());

            }
        });
    }

    private void populateUserWithTheirRoles(List<UserDto> allUsers) {
        allUsers.forEach(user -> {
            List<Roles> usersRoles = getRolesByUserId(uuidPersistenceTransformer.fromString(user.getUserId()));
            user.setRoles(usersRoles);
        });
    }

    private byte[] getUserId(ResultSet resultSet, int _int) throws SQLException {
        return resultSet.getBytes("user_id");
    }

}
