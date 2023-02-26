package com.example.blogapi.users;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.commons.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    String username;
    String password;
    String email;
    String bio;
    String image;

    @ManyToMany(mappedBy = "likedBy")
    List<ArticleEntity> likedArticles;

    @ManyToMany
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    List<UserEntity> following;

    @ManyToMany(mappedBy = "following")
    List<UserEntity> followers;
}
