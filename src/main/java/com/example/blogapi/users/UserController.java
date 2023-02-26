package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO createUserDTO) throws URISyntaxException {
        var createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.created(new URI("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody LoginUserDTO loginUserDTO){
        var savedUser = userService.loginUser(loginUserDTO);
        return ResponseEntity.ok(savedUser);
    }

    @ExceptionHandler(UserService.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserService.UserNotFoundException ex){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
