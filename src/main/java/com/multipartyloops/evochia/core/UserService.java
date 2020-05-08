package com.multipartyloops.evochia.core;

import com.multipartyloops.evochia.core.commons.PasswordService;
import com.multipartyloops.evochia.entities.users.Roles;
import com.multipartyloops.evochia.entities.users.UserDto;
import com.multipartyloops.evochia.persistance.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.multipartyloops.evochia.entities.users.Roles.DEACTIVATED;

@Service
public class UserService {

    private final UserRepository<UserDto> userRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserService(UserRepository<UserDto> userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public UserDto getUserById(String userId) {
        return excludeThePassword(userRepository.getUserById(userId));
    }

    public UserDto getUserByUsername(String username) {
        return excludeThePassword(userRepository.getUserByUsername(username));
    }

    public String addNewUser(String username, String password, String name, String telephone, List<Roles> roles) {

        String userId = UUID.randomUUID().toString();
        UserDto userDto = new UserDto(userId, username, passwordService.encode(password), roles, name, telephone);
        userRepository.storeUser(userDto);
        return userId;
    }

    public void updateUser(UserDto newUser) {

        checkUserIdIsPassed(newUser.getUserId());

        UserDto user = userRepository.getUserById(newUser.getUserId());
        UserDto updatedUser = constructUpdatedUser(newUser, user);

        userRepository.updateUser(updatedUser);
    }

    public List<UserDto> getAllUsers(){
        return userRepository.getAllUsers()
                .stream()
                .map(this::excludeThePassword)
                .collect(Collectors.toList());
    }


    public List<UserDto> getAllUsersByRole(List<Roles> roles){

        return roles
                .stream()
                .map(userRepository::getAllUsersByRole)
                .flatMap(list-> list.stream().map(this::excludeThePassword))
                .distinct()
                .collect(Collectors.toList());
    }

    public void deactivateUser(String userId){
        checkUserIdIsPassed(userId);

        UserDto user = userRepository.getUserById(userId);
        user.setUsername(userId);
        user.setPassword(passwordService.random(8));
        user.setName(null);
        user.setRoles(List.of(DEACTIVATED));
        user.setTelephone(null);

        userRepository.updateUser(user);
    }

    private UserDto excludeThePassword(UserDto user) {
        return new UserDto(user.getUserId(),
                user.getUsername(),
                null,
                user.getRoles(),
                user.getName(),
                user.getTelephone()
        );
    }

    private void checkUserIdIsPassed(String userIdToUpdate) {
        if(userIdToUpdate == null || "".equals(userIdToUpdate)) {
            throw new IllegalArgumentException("Cannot update user with no userId");
        }
    }

    private UserDto constructUpdatedUser(UserDto newUser, UserDto oldUser) {
        return new UserDto(
                newUser.getUserId(),
                oldOrUpdated(oldUser.getUsername(), newUser.getUsername()),
                updatePassword(oldUser.getPassword(), newUser.getPassword()),
                oldRolesOrUpdateRoles(oldUser.getRoles(), newUser.getRoles()),
                oldOrUpdated(oldUser.getName(), newUser.getName()),
                oldOrUpdated(oldUser.getTelephone(), newUser.getTelephone())
        );
    }

    private String oldOrUpdated(String oldValue, String newValue){
        if(newValue == null || "".equals(newValue)){
            return oldValue;
        }
        return newValue;
    }

    private String updatePassword(String oldPassword, String newPassword){
        if(newPassword == null || "".equals(newPassword)){
            return oldPassword;
        }
        return passwordService.encode(newPassword);
    }

    private  List<Roles> oldRolesOrUpdateRoles(List<Roles> oldRoles, List<Roles> newRoles) {
        if (newRoles != null) {
            return newRoles;
        }
        return oldRoles;
    }
}
