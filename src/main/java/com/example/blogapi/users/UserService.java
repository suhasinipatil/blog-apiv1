package com.example.blogapi.users;

import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthTokenService authTokenService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTService jwtService, AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authTokenService = authTokenService;
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO){
        logger.info("Creating user with username: {}", createUserDTO.getUsername());
        var userEntity = userRepository.findByUsername(createUserDTO.getUsername());
        if(userEntity != null){
            logger.warn("User already exits for username: {}", createUserDTO.getUsername());
            throw new UserAlreadyExitsException(userEntity.getUsername());
        }

        var newUserEntity = modelMapper.map(createUserDTO, UserEntity.class);
        newUserEntity.setPassword(passwordEncoder.encode(newUserEntity.getPassword()));
        var savedUser = userRepository.save(newUserEntity);
        logger.info("User created with id: {}", savedUser.getId());

        var userResponseDTO = modelMapper.map(savedUser, UserResponseDTO.class);
        userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
        return userResponseDTO;
    }

    public UserResponseDTO updateUser(Integer userId, CreateUserDTO createUserDTO){
        logger.info("Updating user with id: {}", userId);
        var userEntity = findById(userId);
        if(userEntity == null){
            logger.warn("User not found with id: {}", userId);
            throw new UserNotFoundException(userId);
        }

        userEntity.setEmail(createUserDTO.getEmail());
        userEntity.setUsername(createUserDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        userEntity.setBio(createUserDTO.getBio());
        userEntity.setImage(createUserDTO.getImage());
        var savedUser = userRepository.save(userEntity);
        logger.info("User updated with id: {}", savedUser.getId());

        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO loginUser(LoginUserDTO loginUserDTO, AuthType authType){
        logger.info("Logging in user with username: {}", loginUserDTO.getUsername());
        var userEntity = userRepository.findByUsername(loginUserDTO.getUsername());
        if(userEntity == null){
            logger.warn("User not found with username: {}", loginUserDTO.getUsername());
            throw new UserNotFoundException(loginUserDTO.getUsername());
        }
        var passMatch = passwordEncoder.matches(loginUserDTO.getPassword(), userEntity.getPassword());
        if(!passMatch){
            logger.warn("Incorrect password for username: {}", loginUserDTO.getUsername());
            throw new IllegalArgumentException("Incorrect password");
        }
        logger.info("User logged in with id: {}", userEntity.getId());

        var userResponseDTO = modelMapper.map(userEntity, UserResponseDTO.class);
        switch (authType){
            case JWT :userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
                    break;
            case AUTH_TOKEN : userResponseDTO.setToken(authTokenService.createAuthToken(userEntity).toString());
                    break;
        }
        logger.info("Token created for user with id: {}", userEntity.getId());

        return userResponseDTO;
    }

    public UserEntity findById(Integer userId){
        logger.info("Finding user with id: {}", userId);
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if(userEntity.isEmpty()){
            logger.warn("User not found with id: {}", userId);
            throw new UserNotFoundException(userId);
        }else {
            logger.info("User found with id: {}", userId);
            return userEntity.get();
        }
    }

    public void logoutUser(Integer userId){
        logger.info("Logging out user with id: {}", userId);
        UserEntity userEntity = findById(userId);
        if(userEntity == null){
            logger.warn("User not found with id: {}", userId);
            throw new UserNotFoundException(userId);
        }
        authTokenService.deleteAuthTokenByUserId(userId);
        logger.info("User logged out with id: {}", userId);
    }

    public static class UserNotFoundException extends IllegalArgumentException{
        public UserNotFoundException(Integer userId) {
            super("User with id " + userId + " is not found");
        }

        public UserNotFoundException(String username) {
            super("User with username " + username + " not found");
        }
    }

    public static class UserAlreadyExitsException extends IllegalArgumentException{
        public UserAlreadyExitsException(String username) {
            super("User already exits for " + username);
        }
    }

    public static class IncorrectPasswordException extends IllegalArgumentException{
        public IncorrectPasswordException() {
            super("Incorrect password");
        }
    }

    public static enum AuthType{
        JWT,
        AUTH_TOKEN,
    }
}
