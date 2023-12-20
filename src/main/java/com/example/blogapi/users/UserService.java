package com.example.blogapi.users;

import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthTokenService authTokenService;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTService jwtService, AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authTokenService = authTokenService;
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO){
        var userEntity = userRepository.findByUsername(createUserDTO.getUsername());
        if(userEntity != null){
            throw new UserAlreadyExitsException(userEntity.getUsername());
        }
        var newUserEntity = modelMapper.map(createUserDTO, UserEntity.class);
        newUserEntity.setPassword(passwordEncoder.encode(newUserEntity.getPassword()));
        var savedUser = userRepository.save(newUserEntity);
        var userResponseDTO = modelMapper.map(savedUser, UserResponseDTO.class);
        userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
        return userResponseDTO;
    }

    public UserResponseDTO updateUser(Integer userId, CreateUserDTO createUserDTO){
        var userEntity = findById(userId);
        userEntity.setEmail(createUserDTO.getEmail());
        userEntity.setUsername(createUserDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        userEntity.setBio(createUserDTO.getBio());
        userEntity.setImage(createUserDTO.getImage());
        var savedUser = userRepository.save(userEntity);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO loginUser(LoginUserDTO loginUserDTO, AuthType authType){
        var userEntity = userRepository.findByUsername(loginUserDTO.getUsername());
        if(userEntity == null){
            throw new UserNotFoundException(loginUserDTO.getUsername());
        }
        var passMatch = passwordEncoder.matches(loginUserDTO.getPassword(), userEntity.getPassword());
        if(!passMatch){
            throw new IllegalArgumentException("Incorrect password");
        }
        var userResponseDTO = modelMapper.map(userEntity, UserResponseDTO.class);
        switch (authType){
            case JWT :userResponseDTO.setToken(jwtService.createJWT(userResponseDTO.getId()));
                    break;
            case AUTH_TOKEN : userResponseDTO.setToken(authTokenService.createAuthToken(userEntity).toString());
                    break;
        }

        return userResponseDTO;
    }

    public UserEntity findById(Integer userId){
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if(userEntity.isPresent()){
            return userEntity.get();
        }else {
            throw new UserNotFoundException(userId);
        }
    }

    public void logoutUser(Integer userId){
        UserEntity userEntity = findById(userId);
        if(userEntity == null){
            throw new UserNotFoundException(userId);
        }
        authTokenService.deleteAuthTokenByUserId(userId);
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
