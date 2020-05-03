package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.entities.users.Roles;

import java.util.Set;


public interface UserRepository<T> {

    T getUserById(String id);

    T getUserByUsername(String username);

    void storeUser(T user);

    boolean updateUser(T user);

    Set<T> getAllUsers();

    Set<T> getAllUsersByRole(Roles role);
}
