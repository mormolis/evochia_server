package com.multipartyloops.evochia.core.identity.user;

import com.multipartyloops.evochia.core.identity.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.user.dtos.Roles;
import com.multipartyloops.evochia.core.identity.user.dtos.UserAuthenticationDto;
import com.multipartyloops.evochia.core.identity.user.dtos.UserDto;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthenticationService {

    private final UserService userService;
    private final PasswordService passwordService;

    public UserAuthenticationService(UserService userService, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
    }

    public Optional<UserAuthenticationDto> authenticateUser(String username, String password) {

        try {
            UserDto userByUsername = userService.getUserByUserName(username);
            if (passwordIsValidAndUserNotDeactivated(userByUsername, password)) {
                return Optional.of(new UserAuthenticationDto(userByUsername.getUserId(), userByUsername.getRoles()));
            }
            return Optional.empty();
        } catch (RowNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean passwordIsValidAndUserNotDeactivated(UserDto userByUsername, String password){
        boolean userIsNotDeactivated = userByUsername.getRoles() != null && !userByUsername.getRoles().contains(Roles.DEACTIVATED);
        boolean passwordIsValid = passwordService.passwordsAreTheSame(password, userByUsername.getPassword());
        return passwordIsValid && userIsNotDeactivated;
    }
}
