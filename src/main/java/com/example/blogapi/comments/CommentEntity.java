package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.commons.BaseEntity;
import com.example.blogapi.users.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "comments")
public class CommentEntity extends BaseEntity {
    @Column(nullable = false, length = 100)
    String title;

    @Column(length = 1000)
    String body;

    @ManyToOne
    UserEntity author;

    @ManyToOne
    ArticleEntity articleEntity;
}
