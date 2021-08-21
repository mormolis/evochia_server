package com.multipartyloops.evochia.persistance.identity.user;

import com.multipartyloops.evochia.core.identity.user.dtos.Roles;

import java.util.List;


public interface UserRepository<T> {

    T getUserById(String id);

    T getUserByUsername(String username);

    void storeUser(T user);

    void updateUser(T user);

    List<T> getAllUsers();

    List<T> getAllUsersByRole(Roles role);
}
