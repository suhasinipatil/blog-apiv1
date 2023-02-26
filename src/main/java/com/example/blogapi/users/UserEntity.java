package com.example.blogapi.users;

import com.example.blogapi.commons.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "users")
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    String username;
    String password;
    String bio;
    String image;
}
