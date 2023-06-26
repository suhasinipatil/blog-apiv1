package com.example.blogapi.comments.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCommentDTO {
    String title;
    String body;
    Integer articleId;
    Integer authorId;
}
