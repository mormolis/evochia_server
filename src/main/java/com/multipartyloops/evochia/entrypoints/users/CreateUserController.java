package com.multipartyloops.evochia.entrypoints.users;

import com.multipartyloops.evochia.core.UserService;
import com.multipartyloops.evochia.entities.user.UserDto;
import com.multipartyloops.evochia.entrypoints.users.dtos.CreateUserResponseDto;
import com.multipartyloops.evochia.entrypoints.users.dtos.RequestUserByRolesDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class CreateUserController {

    private final UserService userService;

    public CreateUserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> allUsers() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/by-roles", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> usersByRoles(@RequestBody RequestUserByRolesDto body) {
        List<UserDto> users = userService.getAllUsersByRole(body.getRoles());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public ResponseEntity<CreateUserResponseDto> addUser(@RequestBody UserDto body) {
        String userId = userService.addNewUser(body.getUsername(), body.getPassword(), body.getName(), body.getTelephone(), body.getRoles());
        CreateUserResponseDto createUserResponseDto = new CreateUserResponseDto();
        createUserResponseDto.setUserId(userId);
        return new ResponseEntity<>(createUserResponseDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/id/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUserById (@PathVariable(value = "user_id") String userId) {
        UserDto userById = userService.getUserById(userId);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/username/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUserByUsername (@PathVariable(value = "username") String username) {
        UserDto userById = userService.getUserByUsername(username);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/update", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUser (@RequestBody UserDto body) {
        userService.updateUser(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/user/deactivate/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> deactivateUser(@PathVariable(value = "user_id") String userId) {
        userService.deactivateUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
