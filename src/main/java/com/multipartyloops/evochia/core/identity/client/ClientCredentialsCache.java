package com.multipartyloops.evochia.core.identity.client;

import com.multipartyloops.evochia.core.identity.dtos.ClientCredentialsDto;
import com.multipartyloops.evochia.persistance.identity.clientcredentials.ClientCredentialsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class ClientCredentialsCache {

    private static final int TWENTY_MINUTES = 1200000;
    private final ClientCredentialsRepository<ClientCredentialsDto> clientCredentialsRepository;

    private ConcurrentMap<String, ClientCredentialsDto> cache;

    public ClientCredentialsCache(ClientCredentialsRepository<ClientCredentialsDto> clientCredentialsRepository) {
        this.clientCredentialsRepository = clientCredentialsRepository;
        populateCache();
    }

    ClientCredentialsDto getByClientId(String clientId) {
        return cache.get(clientId);
    }


    @Scheduled(fixedDelay = TWENTY_MINUTES)
    public void populateCache() {
        this.cache = clientCredentialsRepository.getAll()
                .stream()
                .collect(Collectors.toConcurrentMap(ClientCredentialsDto::getClientId, clientCredentialsDto -> clientCredentialsDto));
    }
}
