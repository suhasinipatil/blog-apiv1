package com.example.blogapi.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.blogapi.articles.ArticlesController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@Service
public class JWTService {

    private Algorithm algorithm = Algorithm.HMAC256("Secret signing key (should be in env or config)");

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    public String createJWT(Integer userId){
        return createJWT(userId,
                new Date(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)
                );
    }

    protected String createJWT(Integer userID, Date iat, Date exp){
        logger.info("Creating JWT for user with id: {}", userID);
        String token = JWT.create()
                .withSubject(userID.toString())
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithm);
        logger.info("JWT created successfully");
        return token;
    }

    public Integer getUserIdFromJWT(String jwt){
        logger.info("Decoding JWT");
        var decodedJWT = JWT.decode(jwt);
        var subject = decodedJWT.getSubject();
        if(subject == null){
            logger.error("No subject in JWT");
            throw new JWTDecodeException("No subject in JWT");
        }
        return Integer.parseInt(subject);

    }


}
