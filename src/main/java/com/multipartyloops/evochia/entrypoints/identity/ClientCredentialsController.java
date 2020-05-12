package com.multipartyloops.evochia.entrypoints.identity;

import com.multipartyloops.evochia.core.identity.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.clients.ClientCredentialsService;
import com.multipartyloops.evochia.core.identity.entities.ClientCredentialsDto;
import com.multipartyloops.evochia.entrypoints.identity.entities.ClientCreationRequestBody;
import com.multipartyloops.evochia.entrypoints.identity.entities.ClientValidityCheckRequestBody;
import com.multipartyloops.evochia.entrypoints.identity.entities.ClientValidityCheckResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/clients")
public class ClientCredentialsController {

    private final ClientCredentialsService clientCredentialsService;
    private final PasswordService passwordService;

    public ClientCredentialsController(ClientCredentialsService clientCredentialsService, PasswordService passwordService) {
        this.clientCredentialsService = clientCredentialsService;
        this.passwordService = passwordService;
    }

    @RequestMapping(value = "/client/add", method = RequestMethod.POST)
    public ResponseEntity<ClientCredentialsDto> addUser(@RequestBody ClientCreationRequestBody body) {
        String secret = passwordService.generateRandomPassword(16);
        ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(UUID.randomUUID().toString(), secret, body.getDevice());
        clientCredentialsService.addNewClientCredentials(clientCredentialsDto);
        clientCredentialsDto.setSecret(secret);
        return new ResponseEntity<>(clientCredentialsDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/client/delete/{client_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "client_id") String clientId) {
        clientCredentialsService.deleteCredentialsByClientId(clientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping( value = "/client/validity", method = RequestMethod.POST)
    public ResponseEntity<ClientValidityCheckResponseBody> checkValidity(@RequestBody ClientValidityCheckRequestBody body) {
        boolean isValid = clientCredentialsService.isPairValid(body.getClientId(), body.getSecret());
        return new ResponseEntity<>(new ClientValidityCheckResponseBody(isValid), HttpStatus.OK);
    }
}
