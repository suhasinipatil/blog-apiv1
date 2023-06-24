package com.example.blogapi.security.authtokens;

import com.example.blogapi.users.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AuthTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    //Since for every client a diff token is sent so manytoone
    @ManyToOne
    private UserEntity user;
}
