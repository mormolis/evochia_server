package com.multipartyloops.evochia.entrypoints.users;

import com.multipartyloops.evochia.core.UserService;
import com.multipartyloops.evochia.entities.users.UserDto;
import com.multipartyloops.evochia.entrypoints.users.dtos.CreateUserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class CreateUserController {

    private final UserService userService;

    public CreateUserController(UserService userService) {
        this.userService = userService;
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
}
