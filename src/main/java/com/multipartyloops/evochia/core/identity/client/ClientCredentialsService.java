package com.multipartyloops.evochia.core.identity.client;

import com.multipartyloops.evochia.core.identity.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.dtos.ClientCredentialsDto;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsFormatException;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import com.multipartyloops.evochia.persistance.identity.clientcredentials.ClientCredentialsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientCredentialsService {

    private final ClientCredentialsRepository<ClientCredentialsDto> clientCredentialsRepository;
    private final PasswordService passwordService;
    private final ClientCredentialsCache cache;

    public ClientCredentialsService(ClientCredentialsRepository<ClientCredentialsDto> clientCredentialsRepository, PasswordService passwordService, ClientCredentialsCache cache) {
        this.clientCredentialsRepository = clientCredentialsRepository;
        this.passwordService = passwordService;
        this.cache = cache;
    }

    public boolean isPairValid(String clientId, String secret) {

        ClientCredentialsDto cached = cache.getByClientId(clientId);
        if (cached != null) {
            return passwordService.passwordsAreTheSame(secret, cached.getSecret());
        }

        Optional<ClientCredentialsDto> clientCredentialsDto = retrieveFromDatabase(clientId);
        return clientCredentialsDto.filter(credentialsDto -> passwordService.passwordsAreTheSame(secret, credentialsDto.getSecret())).isPresent();
    }

    public void addNewClientCredentials(ClientCredentialsDto clientCredentialsDto) {

        checkIfDataIsValid(clientCredentialsDto);
        String hashedSecret = passwordService.hashPassword(clientCredentialsDto.getSecret());
        clientCredentialsDto.setSecret(hashedSecret);
        clientCredentialsRepository.storeClientCredentials(clientCredentialsDto);
    }

    public void deleteCredentialsByClientId(String clientId) {
        checkClientIdFormatValidity(clientId);
        clientCredentialsRepository.deleteByClientId(clientId);
    }

    private void checkIfDataIsValid(ClientCredentialsDto clientCredentialsDto) {

        if (clientCredentialsDto.getClientId() == null) {
            throw new InvalidCredentialsFormatException("ClientId cannot be null");
        }

        checkClientIdFormatValidity(clientCredentialsDto.getClientId());

        if (clientCredentialsDto.getSecret() == null) {
            throw new InvalidCredentialsFormatException("Secret must not be null");
        }

        if (clientCredentialsDto.getSecret().length() < 8) {
            throw new InvalidCredentialsFormatException("Secret must be at least 8 characters long");
        }
    }


    private void checkClientIdFormatValidity(String clientId) {
        try {
            UUID.fromString(clientId);
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsFormatException("ClientId should be in the form of UUID");
        }
    }

    private Optional<ClientCredentialsDto> retrieveFromDatabase(String clientId) {
        try {
            return Optional.of(clientCredentialsRepository.getByClientId(clientId));
        } catch (RowNotFoundException ex) {
            return Optional.empty();
        }
    }

}
