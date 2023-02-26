package com.example.blogapi.users;

import com.example.blogapi.users.dto.CreateUserDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    private UserService getUserService(){
        if(userService == null){
            var modelMapper = new ModelMapper();
            userService = new UserService(userRepository, modelMapper);
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
