package com.example.blogapi.security.authtokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, UUID> {
    void deleteByUser_Id(Integer userId);
}
