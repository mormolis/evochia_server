package com.multipartyloops.evochia.core.identity;

import com.multipartyloops.evochia.core.UserService;
import com.multipartyloops.evochia.entities.identity.AccessTokenDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.identity.accesstoken.AccessTokensRepository;

public class IdentityService {

    private AccessTokensRepository<AccessTokenDto> accessTokenRepository;
    private UserService userService;
    private UuidPersistenceTransformer uuidPersistenceTransformer;

}
