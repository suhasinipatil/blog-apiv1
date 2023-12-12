package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.commons.BaseEntity;
import com.example.blogapi.users.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "comments")
@Getter
@Setter
public class CommentEntity extends BaseEntity {
    @Column(length = 1000)
    String body;

    @ManyToOne
    UserEntity author;
}
