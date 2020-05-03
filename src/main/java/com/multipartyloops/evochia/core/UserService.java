package com.multipartyloops.evochia.core;

import com.multipartyloops.evochia.entities.users.Roles;
import com.multipartyloops.evochia.entities.users.UserDto;
import com.multipartyloops.evochia.persistance.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository<UserDto> userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository<UserDto> userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    public UserDto getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
    public String addNewUser(String username, String password, String name, String telephone, List<Roles> roles) {

        String userId = UUID.randomUUID().toString();
        UserDto userDto = new UserDto(userId, username, passwordEncoder.encode(password), roles, name, telephone);
        userRepository.storeUser(userDto);
        return userId;
    }

    public boolean updateUser(String userId,
                              Optional<String> username,
                              Optional<String> password,
                              Optional<String> name,
                              Optional<String> telephone,
                              Optional<List<Roles>> roles) {

        UserDto user = userRepository.getUserById(userId);
        UserDto updatedUser = new UserDto();
        updatedUser.setUserId(userId);
        updatedUser.setName(username.orElse(user.getName()));
        updatedUser.setTelephone(telephone.orElse(user.getTelephone()));
        updatedUser.setUsername(username.orElse(user.getUsername()));
        updatedUser.setRoles(roles.orElse(user.getRoles()));
        updatedUser.setName(name.orElse(user.getName()));

        Optional<String> encryptedPassword = password.flatMap(newPassword -> Optional.of(passwordEncoder.encode(password.get())));
        updatedUser.setPassword(encryptedPassword.orElse(user.getPassword()));
        return userRepository.updateUser(updatedUser);
    }


}
