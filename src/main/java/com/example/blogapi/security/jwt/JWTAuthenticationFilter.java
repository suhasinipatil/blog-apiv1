package com.example.blogapi.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class JWTAuthenticationFilter extends AuthenticationFilter {
    public JWTAuthenticationFilter() {
        super(new JWTAuthenticationManager(), new JWTAuthenticationConverter());

        setSuccessHandler(((request, response, authentication) -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }));
    }

    static class JWTAuthenticationConverter implements AuthenticationConverter{

        @Override
        public Authentication convert(HttpServletRequest request) {
            if(request.getHeader("Authorization") != null){
                var splitValue = request.getHeader("Authorization").split(" ");

                if(splitValue.length != 2){
                    return null;
                }
                String token = splitValue[1];
                return new JWTAuthentication(token);
            }
            return null;
        }
    }

    static class JWTAuthenticationManager implements AuthenticationManager{
        private JWTService jwtService = new JWTService();

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            if(authentication instanceof JWTAuthentication){
                JWTAuthentication jwtAuthentication = (JWTAuthentication) authentication;
                String token = jwtAuthentication.getCredentials();

                if(token != null){
                    var userId = jwtService.getUserIdFromJWT(token);
                    jwtAuthentication.setUserId(userId);

                    return jwtAuthentication;
                }
            }
            return null;
        }
    }
}
