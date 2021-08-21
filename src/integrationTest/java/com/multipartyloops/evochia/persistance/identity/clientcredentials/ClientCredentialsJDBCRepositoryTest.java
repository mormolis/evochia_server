package com.multipartyloops.evochia.persistance.identity.clientcredentials;

import com.multipartyloops.evochia.core.identity.dtos.ClientCredentialsDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientCredentialsJDBCRepositoryTest extends JDBCTest {

    private ClientCredentialsJDBCRepository clientCredentialsJDBCRepository;

    @BeforeEach
    void setup() {
        clientCredentialsJDBCRepository = new ClientCredentialsJDBCRepository(new JdbcTemplate(dataSource), new UuidPersistenceTransformer());
    }

    @Test
    void clientCanBeAddedAndRetrieved() {
        String clientId = UUID.randomUUID().toString();
        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(clientId, "aSecret", "aDevice");

        clientCredentialsJDBCRepository.storeClientCredentials(clientCredentialsDto);

        assertThat(clientCredentialsJDBCRepository.getByClientId(clientId)).isEqualTo(clientCredentialsDto);
    }

    @Test
    void allClientsCanBeRetrieved(){
        List<ClientCredentialsDto> listOfClients = generateThreeRandomClients();
        storeClientsListInTheDB(listOfClients);

        List<ClientCredentialsDto> retrievedCredentials = clientCredentialsJDBCRepository.getAll();

        assertThat(retrievedCredentials).containsAll(listOfClients);
    }

    @Test
    void clientCanBeDeletedByClientId(){
        List<ClientCredentialsDto> listOfClients = generateThreeRandomClients();
        storeClientsListInTheDB(listOfClients);
        ClientCredentialsDto clientToDelete = listOfClients.get(0);

        clientCredentialsJDBCRepository.deleteByClientId(clientToDelete.getClientId());

        assertThatThrownBy(()->clientCredentialsJDBCRepository.getByClientId(clientToDelete.getClientId()))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Client not found");
    }

    private List<ClientCredentialsDto> generateThreeRandomClients() {
        return List.of(
                new ClientCredentialsDto(UUID.randomUUID().toString(), "aSecret", "aDevice"),
                new ClientCredentialsDto(UUID.randomUUID().toString(), "aSecret", "aDevice"),
                new ClientCredentialsDto(UUID.randomUUID().toString(), "aSecret", "aDevice")
        );
    }

    private void storeClientsListInTheDB(List<ClientCredentialsDto> listOfClients) {
        listOfClients
                .forEach(c -> clientCredentialsJDBCRepository.storeClientCredentials(c));
    }

}