package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.entities.user.Roles;

import java.util.List;


public interface UserRepository<T> {

    T getUserById(String id);

    T getUserByUsername(String username);

    void storeUser(T user);

    void updateUser(T user);

    List<T> getAllUsers();

    List<T> getAllUsersByRole(Roles role);
}
