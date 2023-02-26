package com.example.blogapi.security.jwt;

import org.junit.jupiter.api.Test;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JWTServiceTests {

    private  JWTService jwtService = new JWTService();

    @Test
    void canCreateJWTFromUserId(){
        var userId = 1122;
        var jwt = jwtService.createJWT(userId, new Date(1677082), new Date(1677687));

        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTIyIiwiZXhwIjoxNjc3LCJpYXQiOjE2Nzd9.csgksqlpxaXF7XZyBmnaiy38HxMyq3KxbTss2c3Z3Dk", jwt);
    }

    @Test
    void canVerifyJWT(){
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOiIxMTIyIiwiaWF0IjoxNTk3ODk1MTY4LCJleHAiOjIwOTk5OTk5OTh9.oOQjt5e06GQYt-sYY6AUzrv74B1Q3mKFAuyhCILDnT0";
        var userId = jwtService.getUserIdFromJWT(jwt);
        assertEquals(1122, userId);
    }
}
