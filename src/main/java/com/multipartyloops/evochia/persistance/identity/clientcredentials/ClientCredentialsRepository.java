package com.multipartyloops.evochia.persistance.identity.clientcredentials;

import com.multipartyloops.evochia.entities.identity.ClientCredentialsDto;

import java.util.List;

public interface ClientCredentialsRepository<T> {

    void storeClientCredentials(T t);
    T getByClientId(String clientId);
    List<T> getAll();
    void deleteByClientId(String clientId);
}
