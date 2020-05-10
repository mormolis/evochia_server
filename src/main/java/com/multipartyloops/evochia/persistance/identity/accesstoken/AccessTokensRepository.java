package com.multipartyloops.evochia.persistance.identity.accesstoken;

import java.util.List;

public interface AccessTokensRepository<T>{

    T getByAccessToken(String token);
    T getByRefreshToken(String refreshToken);
    List<T> getAllTokens();

    void storeAccessToken(T accessToken);
    void deleteAccessToken(String token);
}
