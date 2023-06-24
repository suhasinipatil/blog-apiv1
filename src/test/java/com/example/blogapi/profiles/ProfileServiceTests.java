package com.example.blogapi.profiles;

import com.example.blogapi.security.authtokens.AuthTokenRepository;
import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import com.example.blogapi.users.UserServiceTests;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProfileServiceTests {

    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthTokenRepository authTokenRepository;
    private UserService userService;

    private UserService getUserService(){
        if(userService == null){
            var modelMapper = new ModelMapper();
            var passwordEncoder = new BCryptPasswordEncoder();
            var jwtService = new JWTService();
            var authTokenService = new AuthTokenService(authTokenRepository);
            userService = new UserService(userRepository, modelMapper, passwordEncoder, jwtService, authTokenService);
        }
        return userService;
    }

    private ProfileService getProfileService(){
        if(profileService == null){
            var modelMapper = new ModelMapper();
            profileService = new ProfileService(userRepository, modelMapper);
        }
        return profileService;
    }

    private UserResponseDTO createUser(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("cde@gmail.com");
        createUserDTO.setUsername("cde");
        createUserDTO.setPassword("cde");
        var savedUser = getUserService().createUser(createUserDTO);
        return savedUser;
    }

    @Test
    public void getAllProfiles(){
        var savedUser = createUser();
        var profileResponseDTOList = getProfileService().getAllProfiles();
        assertNotNull(profileResponseDTOList);
        assertEquals(1, profileResponseDTOList.size());
    }

    @Test
    public void getProfileByUsername(){
        var savedUser = createUser();
        var profileResponseDTO = getProfileService().getProfileByUsername(savedUser.getUsername());
        assertNotNull(profileResponseDTO);
        assertEquals(savedUser.getUsername(), profileResponseDTO.getUsername());
    }
}
