package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.entities.users.Roles;
import com.multipartyloops.evochia.entities.users.UserDto;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class UserJDBCRepository implements UserRepository<UserDto> {

    //naming convention {TABLE_NAME}_{OPERATION}
    private static final String USERS_INSERTION = "INSERT INTO users (user_id, username, password, name, telephone) VALUES (?, ?, ?, ?, ?)";
    private static final String ROLES_INSERTION = "INSERT INTO roles (role_id, user_id, role) VALUES (?, ?, ?)";
    public static final String USERS_SELECT_BY_ID = "SELECT * FROM users WHERE user_id=?";
    public static final String USERS_SELECT_BY_USERNAME = "SELECT * FROM users WHERE username=?";
    public static final String ROLES_SELECT_BY_USER_ID = "SELECT role FROM roles WHERE user_id=?";

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public UserJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public UserDto getUserById(String id) {
        byte[] binaryUserId = uuidPersistenceTransformer.fromString(id);

        List<Roles> roles = jdbcTemplate.query(ROLES_SELECT_BY_USER_ID, this::roleFromResultSet, binaryUserId);

        UserDto user = getUserByIdWithoutRole(binaryUserId);
        user.setRoles(roles);
        return user;
    }

    @Override
    public UserDto getUserByUsername(String username) {

        UserDto user = getUserByUsernameWithoutRole(username);
        List<Roles> roles = jdbcTemplate.query(ROLES_SELECT_BY_USER_ID, (resultSet, _rowNum) -> {
            return Roles.valueOf(resultSet.getString("role"));
        }, uuidPersistenceTransformer.fromString(user.getUserId()));
        user.setRoles(roles);
        return user;
    }

    @Override
    public void storeUser(UserDto user) {

        byte[] binaryUserId = uuidPersistenceTransformer.fromString(user.getUserId());

        jdbcTemplate.update(USERS_INSERTION, binaryUserId, user.getUsername(), user.getPassword(), user.getName(), user.getTelephone());

        for (Roles role : user.getRoles()) {
            jdbcTemplate.update(ROLES_INSERTION, uuidPersistenceTransformer.fromString(UUID.randomUUID().toString()), binaryUserId, role.name());
        }
    }

    @Override
    public boolean updateUser(UserDto user) {
        return false;
    }

    @Override
    public Set<UserDto> getAllUsers() {
        return null;
    }

    @Override
    public Set<UserDto> getAllUsersByRole(Roles role) {
        return null;
    }


    private UserDto getUserByIdWithoutRole(byte[] binaryUserId) {
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
        user.setPassword(resultSet.getString("telephone"));
        return user;
    }

    private  Roles roleFromResultSet(ResultSet resultSet, int _rowNum) throws SQLException {
        return Roles.valueOf(resultSet.getString("role"));
    }
}
