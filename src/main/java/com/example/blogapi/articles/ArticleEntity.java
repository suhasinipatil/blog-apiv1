package com.example.blogapi.articles;


import com.example.blogapi.comments.CommentEntity;
import com.example.blogapi.commons.BaseEntity;
import com.example.blogapi.users.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "articles")
@Getter
@Setter
public class ArticleEntity extends BaseEntity {

    @Column(nullable = false, length = 200)
    String title;
    @Column(nullable = false, length = 8000)
    String body;

    @ManyToOne
    UserEntity author;

    @ManyToMany
    @JoinTable(
                    name = "article_likes",
                    joinColumns = @JoinColumn(name = "article_id"),
                    inverseJoinColumns = @JoinColumn(name = "user_id")
            )
    List<UserEntity> likedBy;

    @OneToMany
    List<CommentEntity> commentEntityList;

}
