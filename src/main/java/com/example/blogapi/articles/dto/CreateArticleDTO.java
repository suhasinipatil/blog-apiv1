package com.example.blogapi.articles.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArticleDTO {
    String title;
    String body;
}
