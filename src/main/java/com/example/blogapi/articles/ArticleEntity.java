package com.example.blogapi.articles;


import com.example.blogapi.commons.BaseEntity;
import com.example.blogapi.users.UserEntity;

import javax.persistence.*;

@Entity(name = "articles")
public class ArticleEntity extends BaseEntity {

    @Column(unique = true, nullable = false, length = 150)
    String slug;
    @Column(nullable = false, length = 200)
    String title;
    String subtitle;
    @Column(nullable = false, length = 8000)
    String body;

    @ManyToOne
    UserEntity author;

}
