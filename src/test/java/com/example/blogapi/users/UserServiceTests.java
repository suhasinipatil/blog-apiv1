package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    private UserService getUserService(){
        if(userService == null){
            var modelMapper = new ModelMapper();
            var passwordEncoder = new BCryptPasswordEncoder();
            userService = new UserService(userRepository, modelMapper, passwordEncoder);
        }
        return userService;
    }

    @Test
    public void createUser(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("cde@gmail.com");
        createUserDTO.setUsername("cde");
        createUserDTO.setPassword("cde");
        var savedUser = getUserService().createUser(createUserDTO);
        assertNotNull(savedUser);
    }
}
