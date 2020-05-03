package com.multipartyloops.evochia.persistance.user;

//naming convention {TABLE_NAME}_{OPERATION}
final class UserSQLStatements {
    static final String USERS_INSERTION = "INSERT INTO users (user_id, username, password, name, telephone) VALUES (?, ?, ?, ?, ?)";
    static final String USERS_SELECT_BY_ID = "SELECT * FROM users WHERE user_id=?";
    static final String USERS_SELECT_BY_USERNAME = "SELECT * FROM users WHERE username=?";
    static final String USERS_UPDATE = "UPDATE users SET username=?, password=?, name=?, telephone=? WHERE user_id=?";
    static final String USERS_SELECT_STAR = "SELECT * FROM users";

    static final String ROLES_SELECT_BY_USER_ID = "SELECT role FROM roles WHERE user_id=?";
    static final String ROLES_INSERTION = "INSERT INTO roles (role_id, user_id, role) VALUES (?, ?, ?)";
    static final String ROLES_DELETE_BY_USER_ID_AND_ROLE = "DELETE FROM roles WHERE user_id=? AND role=?";
    static final String ROLES_SELECT_USER_ID_BY_ROLE = "SELECT user_id FROM roles WHERE role=?";

    private UserSQLStatements() {
    }
}
