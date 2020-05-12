package com.multipartyloops.evochia.core.identity.user;

import com.multipartyloops.evochia.core.identity.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import com.multipartyloops.evochia.core.identity.user.entities.UserDto;
import com.multipartyloops.evochia.entrypoints.exceptions.CannotUpdateDeactivatedUserException;
import com.multipartyloops.evochia.persistance.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final String A_USER_ID = UUID.randomUUID().toString();
    public static final String A_USER_NAME = "aUserName";

    @Mock
    private UserRepository<UserDto> userRepositoryMock;

    @Mock
    private PasswordService passwordServiceMock;

    private UserService userService;


    @BeforeEach
    void setup() {
        userService = new UserService(userRepositoryMock, passwordServiceMock);
    }


    @Test
    void getUserByIdWillCallTheUserRepositoryAndWillReturnUser() {
        UserDto userStoredInRepository = userStoredInRepositoryWithoutThePassword();
        userStoredInRepository.setPassword("aPassword");
        given(userRepositoryMock.getUserById(A_USER_ID))
                .willReturn(userStoredInRepository);

        UserDto userById = userService.getUserById(A_USER_ID);

        then(userRepositoryMock).should().getUserById(A_USER_ID);
        assertThat(userById).isEqualTo(userStoredInRepositoryWithoutThePassword());
    }

    @Test
    void canGetUserByUserName() {
        UserDto expectedUser = userStoredInRepositoryWithoutThePassword();
        expectedUser.setPassword("aPassword");
        given(userRepositoryMock.getUserByUsername(A_USER_NAME))
                .willReturn(expectedUser);

        UserDto userByUsername = userService.getUserByUsernameWithPasswordObfuscation(A_USER_NAME);

        then(userRepositoryMock).should().getUserByUsername(A_USER_NAME);
        assertThat(userByUsername).isEqualTo(userStoredInRepositoryWithoutThePassword());
    }

    @Test
    void addsANewUser() {
        ArgumentCaptor<UserDto> argumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        willDoNothing().given(userRepositoryMock).storeUser(argumentCaptor.capture());
        given(passwordServiceMock.hashPassword("aPassword")).willReturn("anEncodedPassword");

        String userId = userService.addNewUser(A_USER_NAME, "aPassword", "aName", "aTelephone", new ArrayList<>());

        UserDto capturedUser = argumentCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo("aName");
        assertThat(capturedUser.getPassword()).isEqualTo("anEncodedPassword");
        assertThat(capturedUser.getTelephone()).isEqualTo("aTelephone");
        assertThat(capturedUser.getRoles()).isEqualTo(new ArrayList<>());
        assertThat(capturedUser.getUserId()).isEqualTo(userId);
    }

    @Test
    void updatesTelephoneAndPassword() {
        UserDto newUserDto = new UserDto(A_USER_ID, null, "aNewPassword", null, null, "aTelephone");
        UserDto oldUserDto = new UserDto(A_USER_ID, "aUsername", "anOldPass", new ArrayList<>(), "aName", "anOldTelephone");
        UserDto expected = new UserDto(A_USER_ID, "aUsername", "aNewHashedPass", new ArrayList<>(), "aName", "aTelephone");
        given(userRepositoryMock.getUserById(A_USER_ID)).willReturn(oldUserDto);
        given(passwordServiceMock.hashPassword("aNewPassword")).willReturn("aNewHashedPass");

        userService.updateUser(newUserDto);

        then(userRepositoryMock).should().updateUser(expected);
    }

    @Test
    void updateUserWithoutUserIdThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> userService.updateUser(new UserDto()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update user with no userId");
    }

    @Test
    void canGetAllUsers() {
        given(userRepositoryMock.getAllUsers()).willReturn(allUsers());

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers).isEqualTo(allUserWithoutThePassword());
    }

    @Test
    void canGetAllUserByRoles() {
        given(userRepositoryMock.getAllUsersByRole(Roles.ADMIN)).willReturn(allUsers());
        given(userRepositoryMock.getAllUsersByRole(Roles.FINANCE)).willReturn(almostDifferentSetOfUsers());

        List<UserDto> allUsersByRole = userService.getAllUsersByRole(List.of(Roles.ADMIN, Roles.FINANCE));

        assertThat(allUsersByRole).isEqualTo(combineListsWithoutDuplicatesAndPasswords());
    }

    @Test
    void userIsDeactivatedByAssigningTheRoleDeactivatedToTheirUserId() {
        UserDto userDetails = new UserDto(A_USER_ID, "aUsername", "anOldPass", List.of(Roles.ADMIN, Roles.STAFF), "aName", "anOldTelephone");
        UserDto deactivatedUser = new UserDto(A_USER_ID, A_USER_ID, "random_pass", List.of(Roles.DEACTIVATED), null, null);
        given(userRepositoryMock.getUserById(A_USER_ID)).willReturn(userDetails);
        given(passwordServiceMock.generateRandomPassword(8)).willReturn("random_pass");

        userService.deactivateUser(A_USER_ID);

        then(userRepositoryMock).should().updateUser(deactivatedUser);
    }

    @Test
    void userCannotBeUpdatedWhenDeactivated() {
        UserDto userDetails = new UserDto(A_USER_ID, A_USER_ID, "a_pass", List.of(Roles.DEACTIVATED), null, null);
        given(userRepositoryMock.getUserById(A_USER_ID)).willReturn(userDetails);

        assertThatThrownBy(() -> userService.updateUser(userDetails))
                .isInstanceOf(CannotUpdateDeactivatedUserException.class)
                .hasMessage("User is deactivated.");

    }

    @Test
    void deactivateUserWithoutUserIdThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> userService.deactivateUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update user with no userId");

        assertThatThrownBy(() -> userService.deactivateUser(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update user with no userId");
    }


    private List<UserDto> combineListsWithoutDuplicatesAndPasswords() {
        return List.of(
                new UserDto("1", null, null, null, null, null),
                new UserDto("2", null, null, null, null, null),
                new UserDto("3", null, null, null, null, null),
                new UserDto("4", null, null, null, null, null),
                new UserDto("5", null, null, null, null, null)
        );
    }

    private List<UserDto> allUsers() {
        return List.of(
                new UserDto("1", null, "password", null, null, null),
                new UserDto("2", null, "password", null, null, null),
                new UserDto("3", null, "password", null, null, null),
                new UserDto("4", null, "password", null, null, null)
        );
    }

    private List<UserDto> almostDifferentSetOfUsers() {
        return List.of(
                new UserDto("1", null, "password", null, null, null),
                new UserDto("2", null, "password", null, null, null),
                new UserDto("3", null, "password", null, null, null),
                new UserDto("5", null, "password", null, null, null)
        );
    }

    private List<UserDto> allUserWithoutThePassword() {
        return List.of(
                new UserDto("1", null, null, null, null, null),
                new UserDto("2", null, null, null, null, null),
                new UserDto("3", null, null, null, null, null),
                new UserDto("4", null, null, null, null, null)
        );
    }

    private UserDto userStoredInRepositoryWithoutThePassword() {
        UserDto userStoredInRepositoryWithoutThePassword = new UserDto();
        userStoredInRepositoryWithoutThePassword.setUserId(A_USER_ID);
        userStoredInRepositoryWithoutThePassword.setUsername(A_USER_NAME);
        return userStoredInRepositoryWithoutThePassword;
    }

}