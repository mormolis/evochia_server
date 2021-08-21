package com.multipartyloops.evochia.entrypoints.identity;

import com.multipartyloops.evochia.core.identity.accesstoken.AccessTokenService;
import com.multipartyloops.evochia.core.identity.dtos.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.dtos.ValidateTokenResponseDto;
import com.multipartyloops.evochia.entrypoints.identity.dtos.AccessTokenIssuanceRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/access-token")
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    public AccessTokenController(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @RequestMapping(value = "/issue", method = RequestMethod.POST)
    public ResponseEntity<AccessTokenDto> issueAccessToken(@RequestBody AccessTokenIssuanceRequestBody body) {
        AccessTokenDto accessTokenDto = accessTokenService.issueToken(body.getClientId(), body.getSecret(), body.getUsername(), body.getPassword());
        return new ResponseEntity<>(accessTokenDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/validate/{access_token}", method = RequestMethod.POST)
    public ResponseEntity<ValidateTokenResponseDto> validateAccessToken(@RequestHeader String clientId, @RequestHeader String secret, @PathVariable("access_token") String accessToken) {
        ValidateTokenResponseDto validateTokenResponseDto = accessTokenService.validateToken(clientId, secret, accessToken);
        return new ResponseEntity<>(validateTokenResponseDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh/{refresh_token}", method = RequestMethod.POST)
    public ResponseEntity<AccessTokenDto> refreshAccessToken(@RequestHeader String clientId, @RequestHeader String secret, @PathVariable("refresh_token") String refreshToken) {
        AccessTokenDto accessTokenDto = accessTokenService.issueAccessTokenByRefreshToken(clientId, secret, refreshToken);
        return new ResponseEntity<>(accessTokenDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{access_token}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAccessToken(@RequestHeader String clientId, @RequestHeader String secret, @PathVariable("access_token") String accessToken) {
        accessTokenService.deleteAccessToken(clientId, secret, accessToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}