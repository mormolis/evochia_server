package com.multipartyloops.evochia.core.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.commons.RolesConverter;
import com.multipartyloops.evochia.core.identity.clients.ClientCredentialsService;
import com.multipartyloops.evochia.core.identity.entities.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.entities.ValidateTokenResponseDto;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsException;
import com.multipartyloops.evochia.core.identity.user.UserAuthenticationService;
import com.multipartyloops.evochia.core.identity.user.entities.UserAuthenticationDto;
import com.multipartyloops.evochia.persistance.identity.accesstoken.AccessTokensRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccessTokenService {

    private static final long TOKEN_EXPIRY_IN_SECONDS = 3600;
    private static final long REFRESH_TOKEN_EXPIRY_IN_SECONDS = 7200;

    private final AccessTokensRepository<AccessTokenDto> accessTokenRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final ClientCredentialsService clientCredentialsService;
    private final RolesConverter rolesConverter;

    public AccessTokenService(AccessTokensRepository<AccessTokenDto> accessTokenRepository, UserAuthenticationService userAuthenticationService, ClientCredentialsService clientCredentialsService, RolesConverter rolesConverter) {
        this.accessTokenRepository = accessTokenRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.clientCredentialsService = clientCredentialsService;
        this.rolesConverter = rolesConverter;
    }

    public AccessTokenDto issueToken(String clientId, String secret, String username, String password) {

        checkClientCredentialsValidity(clientId, secret);

        Optional<UserAuthenticationDto> userAuthenticationDto = userAuthenticationService.authenticateUser(username, password);

        return userAuthenticationDto.map(userAuthInfo -> {
            String roles = rolesConverter.fromList(userAuthInfo.getRoles());
            AccessTokenDto accessTokenDto = constructAccessTokenResponse(clientId, userAuthInfo.getUserId(), roles);
            accessTokenRepository.storeAccessToken(accessTokenDto);
            return accessTokenDto;
        }).orElseThrow(() -> new InvalidCredentialsException("Invalid Username or Password"));
    }

    public AccessTokenDto issueAccessTokenByRefreshToken(String clientId, String secret, String refreshToken) {

        checkClientCredentialsValidity(clientId, secret);

        AccessTokenDto byRefreshToken = accessTokenRepository.getByRefreshToken(refreshToken);
        checkIfRefreshTokenIsExpired(byRefreshToken);

        AccessTokenDto newAccessToken = constructAccessTokenResponse(byRefreshToken.getClientId(), byRefreshToken.getUserId(), byRefreshToken.getRoles());
        accessTokenRepository.storeAccessToken(newAccessToken);
        accessTokenRepository.deleteAccessToken(byRefreshToken.getToken());
        return newAccessToken;
    }

    public ValidateTokenResponseDto validateToken(String clientId, String secret, String accessToken) {

        checkClientCredentialsValidity(clientId, secret);
        return validateTokenWithoutClientCredentials(accessToken);
    }

    public  ValidateTokenResponseDto validateTokenWithoutClientCredentials(String accessToken) {
        AccessTokenDto byAccessToken = accessTokenRepository.getByAccessToken(accessToken);
        ValidateTokenResponseDto validateTokenResponseDto = new ValidateTokenResponseDto(false, byAccessToken.getUserId(), byAccessToken.getClientId(), byAccessToken.getRoles());
        if (accessTokenHasNotExpired(byAccessToken)) {
            validateTokenResponseDto.setValid(true);
        }
        return validateTokenResponseDto;
    }

    public void deleteAccessToken(String clientId, String secret, String accessToken){

        checkClientCredentialsValidity(clientId, secret);
        accessTokenRepository.deleteAccessToken(accessToken);
    }

    private boolean accessTokenHasNotExpired(AccessTokenDto byAccessToken) {
        return byAccessToken.getTokenExpiry().isAfter(LocalDateTime.now());
    }

    private void checkIfRefreshTokenIsExpired(AccessTokenDto byRefreshToken) {
        if (!byRefreshToken.getRefreshTokenExpiry().isAfter(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Refresh token has expired");
        }
    }

    private void checkClientCredentialsValidity(String clientId, String secret) {
        if (!clientCredentialsService.isPairValid(clientId, secret)) {
            throw new InvalidCredentialsException("Invalid ClientId or Secret");
        }
    }

    private AccessTokenDto constructAccessTokenResponse(String clientId, String userId, String roles) {
        return new AccessTokenDto(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plus(TOKEN_EXPIRY_IN_SECONDS, ChronoUnit.SECONDS),
                UUID.randomUUID().toString(),
                LocalDateTime.now().plus(REFRESH_TOKEN_EXPIRY_IN_SECONDS, ChronoUnit.SECONDS),
                userId,
                clientId,
                roles
        );
    }
}
