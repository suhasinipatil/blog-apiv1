package com.example.blogapi.users;

import com.example.blogapi.security.authtokens.AuthTokenRepository;
import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.dto.CreateUserDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;
    private UserService userService;

    public UserService getUserService(){
        if(userService == null){
            var modelMapper = new ModelMapper();
            var passwordEncoder = new BCryptPasswordEncoder();
            var jwtService = new JWTService();
            var authTokenService = new AuthTokenService(authTokenRepository);
            userService = new UserService(userRepository, modelMapper, passwordEncoder, jwtService, authTokenService);
        }
        return userService;
    }

    public CreateUserDTO getCreateUserDTO(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("cde@gmail.com");
        createUserDTO.setUsername("cde");
        createUserDTO.setPassword("cde");
        return createUserDTO;
    }

    @Test
    public void createUser(){
        CreateUserDTO createUserDTO = getCreateUserDTO();
        var savedUser = getUserService().createUser(createUserDTO);
        assertNotNull(savedUser);
    }

    @Test
    public void updateUser(){
        CreateUserDTO createUserDTO = getCreateUserDTO();
        var savedUser = getUserService().createUser(createUserDTO);

        CreateUserDTO updateUserDTO = new CreateUserDTO();
        updateUserDTO.setEmail("newemail@gmail.com");
        updateUserDTO.setUsername("newusername");
        updateUserDTO.setPassword("newpassword");
        updateUserDTO.setBio("newbio");
        updateUserDTO.setImage("newimage");
        var savedUser1 = getUserService().updateUser(savedUser.getId(), updateUserDTO);
        assertNotNull(savedUser1);
    }
}
