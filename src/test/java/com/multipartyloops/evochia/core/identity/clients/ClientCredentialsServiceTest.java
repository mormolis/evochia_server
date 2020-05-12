package com.multipartyloops.evochia.core.identity.clients;

import com.multipartyloops.evochia.core.identity.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsFormatException;
import com.multipartyloops.evochia.core.identity.entities.ClientCredentialsDto;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import com.multipartyloops.evochia.persistance.identity.clientcredentials.ClientCredentialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsServiceTest {

    public static final String A_VALID_CLIENT_ID = UUID.randomUUID().toString();
    public static final String AN_INVALID_CLIENT_ID = UUID.randomUUID().toString();
    public static final String A_VALID_UNHASHED_SECRET = "a_valid_unhashed_secret";
    private static final String AN_INVALID_UNHASHED_SECRET = "an_invalid_unhashed_secret";
    public static final String A_HASHED_SECRET = "aHashedSecret";

    @Mock
    private ClientCredentialsRepository<ClientCredentialsDto> clientCredentialsRepositoryMock;

    @Mock
    private PasswordService passwordServiceMock;

    @Mock
    private ClientCredentialsCache cacheMock;

    private ClientCredentialsService clientCredentialsService;

    @BeforeEach
    void setup(){
        clientCredentialsService = new ClientCredentialsService(clientCredentialsRepositoryMock, passwordServiceMock, cacheMock);
    }


    @Test
    void clientCredentialsAreValid_returnsValidWhenFoundInCacheAndPasswordMatches(){
        ClientCredentialsDto cachedResponse = new ClientCredentialsDto(A_VALID_CLIENT_ID, "aHashedSecret", "aDevice");
        given(cacheMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(cachedResponse);
        given(passwordServiceMock.passwordsAreTheSame(A_VALID_UNHASHED_SECRET, "aHashedSecret")).willReturn(true);

        boolean validityCheck = clientCredentialsService.isPairValid(A_VALID_CLIENT_ID, A_VALID_UNHASHED_SECRET);

        then(clientCredentialsRepositoryMock).shouldHaveNoInteractions();
        assertThat(validityCheck).isTrue();
    }

    @Test
    void clientCredentialsAreValid_whenClientIdFoundInCacheButPasswordIsNotValidACallToDbShouldNotHappen(){
        ClientCredentialsDto cachedResponse = new ClientCredentialsDto(A_VALID_CLIENT_ID, "aHashedSecret", "aDevice");
        given(cacheMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(cachedResponse);
        given(passwordServiceMock.passwordsAreTheSame(AN_INVALID_UNHASHED_SECRET, cachedResponse.getSecret())).willReturn(false);

        boolean validityCheck = clientCredentialsService.isPairValid(A_VALID_CLIENT_ID, AN_INVALID_UNHASHED_SECRET);

        then(clientCredentialsRepositoryMock).shouldHaveNoInteractions();
        assertThat(validityCheck).isFalse();
    }

    @Test
    void clientCredentialsAreValid_whenClientIdIsNotInCacheACallToDBWillHappen(){
        ClientCredentialsDto dbResponse = new ClientCredentialsDto(A_VALID_CLIENT_ID, "aHashedSecret", "aDevice");
        given(cacheMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(null);
        given(clientCredentialsRepositoryMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(dbResponse);
        given(passwordServiceMock.passwordsAreTheSame(A_VALID_UNHASHED_SECRET, dbResponse.getSecret())).willReturn(true);

        boolean validityCheck = clientCredentialsService.isPairValid(A_VALID_CLIENT_ID, A_VALID_UNHASHED_SECRET);

        then(clientCredentialsRepositoryMock).should().getByClientId(A_VALID_CLIENT_ID);
        assertThat(validityCheck).isTrue();
    }

    @Test
    void clientCredentialsAreValid_whenClientIdCannotBeFoundWillReturnFalse(){
        given(cacheMock.getByClientId(AN_INVALID_CLIENT_ID)).willReturn(null);
        given(clientCredentialsRepositoryMock.getByClientId(AN_INVALID_CLIENT_ID)).willThrow(new RowNotFoundException("Invalid Client"));

        boolean validityCheck = clientCredentialsService.isPairValid(AN_INVALID_CLIENT_ID, any(String.class));

        then(passwordServiceMock).shouldHaveNoInteractions();
        assertThat(validityCheck).isFalse();
    }

    @Test
    void clientCredentialsAreValid_aValidClientIdNotInCacheWithWrongSecretWillReturnFalse(){
        ClientCredentialsDto dbResponse = new ClientCredentialsDto(A_VALID_CLIENT_ID, A_HASHED_SECRET, "aDevice");
        given(cacheMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(null);
        given(clientCredentialsRepositoryMock.getByClientId(A_VALID_CLIENT_ID)).willReturn(dbResponse);
        given(passwordServiceMock.passwordsAreTheSame(AN_INVALID_UNHASHED_SECRET, dbResponse.getSecret())).willReturn(false);

        boolean validityCheck = clientCredentialsService.isPairValid(A_VALID_CLIENT_ID, AN_INVALID_UNHASHED_SECRET);

        then(clientCredentialsRepositoryMock).should().getByClientId(A_VALID_CLIENT_ID);
        assertThat(validityCheck).isFalse();
    }

    @Test
    void canPersistNewClientCredentialsWithHashedPassword(){

        ClientCredentialsDto newSetOfCredentials = new ClientCredentialsDto(A_VALID_CLIENT_ID, A_VALID_UNHASHED_SECRET, "");
        ClientCredentialsDto expected = new ClientCredentialsDto(A_VALID_CLIENT_ID, A_HASHED_SECRET, "");
        given(passwordServiceMock.hashPassword(A_VALID_UNHASHED_SECRET)).willReturn(A_HASHED_SECRET);

        clientCredentialsService.addNewClientCredentials(newSetOfCredentials);

        then(clientCredentialsRepositoryMock).should().storeClientCredentials(newSetOfCredentials);
    }

    @Test
    void clientIdCannotBeNullWhenPersisting(){

        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(null, "secret", "device");

        assertThatThrownBy(()-> clientCredentialsService.addNewClientCredentials(clientCredentialsDto))
                .isInstanceOf(InvalidCredentialsFormatException.class)
                .hasMessage("ClientId cannot be null");
    }

    @Test
    void clientIdMustBeInFormOfUUIDWhenPersisting(){
        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto("invalidClientId", "secret", "device");

        assertThatThrownBy(()-> clientCredentialsService.addNewClientCredentials(clientCredentialsDto))
                .isInstanceOf(InvalidCredentialsFormatException.class)
                .hasMessage("ClientId should be in the form of UUID");
    }

    @Test
    void clientSecretMustNotBeNull(){
        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(UUID.randomUUID().toString(), null, "device");

        assertThatThrownBy(()-> clientCredentialsService.addNewClientCredentials(clientCredentialsDto))
                .isInstanceOf(InvalidCredentialsFormatException.class)
                .hasMessage("Secret must not be null");
    }

    @Test
    void clientSecretShouldBeAtLeastEightCharachters(){
        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(UUID.randomUUID().toString(), "1234567", "device");

        assertThatThrownBy(()-> clientCredentialsService.addNewClientCredentials(clientCredentialsDto))
                .isInstanceOf(InvalidCredentialsFormatException.class)
                .hasMessage("Secret must be at least 8 characters long");
    }

    @Test
    void clientCredentialsCanBeDeletedByClientId(){

        clientCredentialsService.deleteCredentialsByClientId(A_VALID_CLIENT_ID);

        then(clientCredentialsRepositoryMock).should().deleteByClientId(A_VALID_CLIENT_ID);
    }
}