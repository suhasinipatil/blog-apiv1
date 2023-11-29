package com.example.blogapi.articles.dto;

import com.example.blogapi.comments.dto.ResponseCommentDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseArticleDTO {
    String slug;
    String title;
    String subtitle;
    String body;
    String author;
    List<ResponseCommentDTO> commentEntities;
}
