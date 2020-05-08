package com.multipartyloops.evochia.core;

import com.multipartyloops.evochia.entities.users.Roles;
import com.multipartyloops.evochia.entities.users.UserDto;
import com.multipartyloops.evochia.persistance.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoderMock;

    private UserService userService;


    @BeforeEach
    void setup() {
        userService = new UserService(userRepositoryMock, passwordEncoderMock);
    }


    @Test
    void getUserByIdWillCallTheUserRepositoryAndWillReturnUser() {
        UserDto expectedUser = new UserDto();
        expectedUser.setUserId(A_USER_ID);
        given(userRepositoryMock.getUserById(A_USER_ID))
                .willReturn(expectedUser);

        UserDto userById = userService.getUserById(A_USER_ID);

        then(userRepositoryMock).should().getUserById(A_USER_ID);
        assertThat(userById).isEqualTo(expectedUser);

    }

    @Test
    void canGetUserByUserName() {
        UserDto expectedUser = new UserDto();
        expectedUser.setUsername(A_USER_NAME);
        given(userRepositoryMock.getUserByUsername(A_USER_NAME))
                .willReturn(expectedUser);

        UserDto userByUsername = userService.getUserByUsername(A_USER_NAME);

        then(userRepositoryMock).should().getUserByUsername(A_USER_NAME);
        assertThat(userByUsername).isEqualTo(expectedUser);
    }

    @Test
    void addsANewUser() {
        ArgumentCaptor<UserDto> argumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        willDoNothing().given(userRepositoryMock).storeUser(argumentCaptor.capture());
        given(passwordEncoderMock.encode("aPassword")).willReturn("anEncodedPassword");

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
        given(passwordEncoderMock.encode("aNewPassword")).willReturn("aNewHashedPass");

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
        given(userRepositoryMock.getAllUsersByRole(Roles.FINANCE)).willReturn(allUsers());

        List<UserDto> allUsersByRole = userService.getAllUsersByRole(Roles.ADMIN, Roles.FINANCE);

        assertThat(allUsersByRole).isEqualTo(combineListsWithoutThePassword());
    }

    private List<UserDto> combineListsWithoutThePassword() {
        List<UserDto> list = new ArrayList<>(allUserWithoutThePassword());
        list.addAll(allUserWithoutThePassword());
        return list;
    }

    private List<UserDto> allUsers() {
        return List.of(
                new UserDto("1", null, "password", null, null, null),
                new UserDto("2", null, "password", null, null, null),
                new UserDto("3", null, "password", null, null, null),
                new UserDto("4", null, "password", null, null, null)
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


}