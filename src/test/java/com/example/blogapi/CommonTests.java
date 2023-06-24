package com.example.blogapi;

import com.example.blogapi.security.authtokens.AuthTokenRepository;
import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CommonTests {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;
    public UserService userService;

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


}
