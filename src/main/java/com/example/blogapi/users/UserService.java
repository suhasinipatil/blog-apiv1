package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.LoginUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(@Autowired UserRepository userRepository, @Autowired ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO){
        var userEntity = modelMapper.map(createUserDTO, UserEntity.class);
        var savedUser = userRepository.save(userEntity);
        var userResponseDTO = modelMapper.map(savedUser, UserResponseDTO.class);
        return userResponseDTO;
    }

    public UserResponseDTO loginUser(LoginUserDTO loginUserDTO){
        var userEntity = userRepository.findByUsername(loginUserDTO.getUsername());
        if(userEntity == null){
            throw new UserNotFoundException(loginUserDTO.getUsername());
        }
        if(!userEntity.getPassword().equals(loginUserDTO.getPassword())){
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

    public static class IncorrectPasswordException extends IllegalArgumentException{
        public IncorrectPasswordException() {
            super("Incorrect password");
        }
    }

}
