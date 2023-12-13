package com.example.blogapi.users;

import com.example.blogapi.commons.dto.ErrorMessage;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody LoginUserDTO loginUserDTO,
                                                     @RequestParam(name = "token", defaultValue = "auth_token") String token)
    {
        var authType = UserService.AuthType.JWT;
        if(token.equals("auth_token")){
            authType = UserService.AuthType.AUTH_TOKEN;
        }
        var savedUser = userService.loginUser(loginUserDTO, authType);
        return ResponseEntity.ok(savedUser);
    }

    @ExceptionHandler(UserService.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserService.UserNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @ExceptionHandler(UserService.UserAlreadyExitsException.class)
    public ResponseEntity<String> handleAlreadyExitsException(UserService.UserAlreadyExitsException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User already exists");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
