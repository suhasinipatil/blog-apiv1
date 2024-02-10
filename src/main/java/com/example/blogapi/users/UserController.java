package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("")
    public ResponseEntity<?> userAction(@RequestParam String type, @RequestBody(required = false) CreateUserDTO createUserDTO, @RequestBody(required = false) LoginUserDTO loginUserDTO, @RequestParam(name = "token", defaultValue = "auth_token", required = false) String token, @AuthenticationPrincipal Integer userId) throws URISyntaxException{
        logger.info("Received request in userAction with type: {}", type);
        switch (type) {
            case "create":
                return createUser(createUserDTO);
            case "login":
                return loginUser(loginUserDTO, token);
            case "logout":
                return logoutUser(userId);
            default:
                logger.warn("Invalid type parameter: {}", type);
                return ResponseEntity.badRequest().body("Invalid type parameter");
        }
    }

    private ResponseEntity<String> logoutUser(Integer userId){
        if(userId != null){
            logger.info("Logging out user with id: {}", userId);
            userService.logoutUser(userId);
            return ResponseEntity.accepted().body("User logged out successfully");
        }
        logger.warn("Invalid user id");
        return ResponseEntity.badRequest().body("Invalid user id");
    }

    private ResponseEntity<UserResponseDTO> createUser(CreateUserDTO createUserDTO) throws URISyntaxException {
        if(createUserDTO != null){
            logger.info("Creating user");
            var createdUser = userService.createUser(createUserDTO);
            logger.info("User created with id: {}", createdUser.getId());
            return ResponseEntity.created(new URI("/users/" + createdUser.getId())).body(createdUser);
        }
        logger.warn("Invalid user data");
        return ResponseEntity.badRequest().body(null);
    }

    private ResponseEntity<UserResponseDTO> loginUser(LoginUserDTO loginUserDTO, String token){
        if(loginUserDTO != null){
            logger.info("Logging in user with username: {0}" + loginUserDTO.getUsername());
            var authType = UserService.AuthType.JWT;
            if(token.equals("auth_token")){
                authType = UserService.AuthType.AUTH_TOKEN;
            }
            var savedUser = userService.loginUser(loginUserDTO, authType);
            logger.info("User logged in with id: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);
        }
        logger.warn("Invalid user data");
        return ResponseEntity.badRequest().body(null);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer userId, @RequestBody CreateUserDTO createUserDTO){
        logger.info("Updating user with id: {}", userId);
        var updatedUser = userService.updateUser(userId, createUserDTO);
        return ResponseEntity.ok(updatedUser);
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
