package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
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
        return userResponseDTO;
    }

    public UserResponseDTO loginUser(LoginUserDTO loginUserDTO){
        var userEntity = userRepository.findByUsername(loginUserDTO.getUsername());
        if(userEntity == null){
            throw new UserNotFoundException(loginUserDTO.getUsername());
        }
        var passMatch = passwordEncoder.matches(loginUserDTO.getPassword(), userEntity.getPassword());
        if(!passMatch){
            throw new IllegalArgumentException("Incorrect password");
        }
        var userResponseDTO = modelMapper.map(userEntity, UserResponseDTO.class);
        return userResponseDTO;
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

}
