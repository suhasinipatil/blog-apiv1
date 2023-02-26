package com.example.blogapi.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDTO {
    String username;
    String password;
    String email;
}
