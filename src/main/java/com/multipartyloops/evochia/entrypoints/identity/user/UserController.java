package com.multipartyloops.evochia.entrypoints.identity.user;

import com.multipartyloops.evochia.configuration.evochiaauthtool.AuthRequirement;
import com.multipartyloops.evochia.core.identity.user.UserService;
import com.multipartyloops.evochia.core.identity.user.dtos.Roles;
import com.multipartyloops.evochia.core.identity.user.dtos.UserDto;
import com.multipartyloops.evochia.entrypoints.identity.user.dtos.CreateUserResponseDto;
import com.multipartyloops.evochia.entrypoints.identity.user.dtos.RequestUserByRolesDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<List<UserDto>> allUsers(@RequestHeader Map<String,String> headers) {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/by-roles", method = RequestMethod.GET)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<List<UserDto>> usersByRoles(@RequestHeader Map<String,String> headers, @RequestBody RequestUserByRolesDto body) {
        List<UserDto> users = userService.getAllUsersByRole(body.getRoles());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<CreateUserResponseDto> addUser(@RequestHeader Map<String,String> headers, @RequestBody UserDto body) {
        String userId = userService.addNewUser(body.getUsername(), body.getPassword(), body.getName(), body.getTelephone(), body.getRoles());
        CreateUserResponseDto createUserResponseDto = new CreateUserResponseDto();
        createUserResponseDto.setUserId(userId);
        return new ResponseEntity<>(createUserResponseDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/id/{user_id}", method = RequestMethod.GET)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<UserDto> getUserById (@RequestHeader Map<String,String> headers, @PathVariable(value = "user_id") String userId) {
        UserDto userById = userService.getUserById(userId);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/username/{username}", method = RequestMethod.GET)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<UserDto> getUserByUsername (@RequestHeader Map<String,String> headers, @PathVariable(value = "username") String username) {
        UserDto userById = userService.getUserByUsernameWithPasswordObfuscation(username);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/update", method = RequestMethod.PATCH)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> updateUser (@RequestHeader Map<String,String> headers, @RequestBody UserDto body) {
        userService.updateUser(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/user/deactivate/{user_id}", method = RequestMethod.PUT)
    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> deactivateUser(@RequestHeader Map<String,String> headers, @PathVariable(value = "user_id") String userId) {
        userService.deactivateUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
