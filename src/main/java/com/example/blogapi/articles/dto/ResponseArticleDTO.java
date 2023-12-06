package com.example.blogapi.articles.dto;

import com.example.blogapi.comments.dto.ResponseCommentDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseArticleDTO {
    Integer id;
    String title;
    String body;
    String author;
    Integer authorId;
    List<ResponseCommentDTO> commentEntities;
}
