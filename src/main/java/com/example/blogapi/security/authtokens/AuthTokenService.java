package com.example.blogapi.security.authtokens;

import com.example.blogapi.articles.ArticlesController;
import com.example.blogapi.users.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthTokenService {
    private final AuthTokenRepository authTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenService.class);
    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    public UUID createAuthToken(UserEntity userEntity){
        logger.info("Creating auth token for user: {}", userEntity.getUsername());
        AuthTokenEntity authTokenEntity = new AuthTokenEntity();
        authTokenEntity.setUser(userEntity);
        var savedAuthToken = authTokenRepository.save(authTokenEntity);
        logger.info("Token created for user: {}", userEntity.getUsername());
        return savedAuthToken.getId();
    }

    public void deleteAuthTokenByUserId(Integer userId){
        logger.info("Deleting auth token for user: {}", userId);
        authTokenRepository.deleteByUser_Id(userId);
        logger.info("Token deleted for user: {}", userId);
    }

    public Integer getUserIdFromAuthToken(UUID authToken){
        logger.info("Getting user id from auth token: {}", authToken);
        var savedAuthToken = authTokenRepository.findById(authToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid Auth Token"));
        return savedAuthToken.getUser().getId();
    }
}
