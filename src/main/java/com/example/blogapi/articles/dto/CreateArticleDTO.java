package com.example.blogapi.articles.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArticleDTO {
    String slug;
    String title;
    String subtitle;
    String body;
}
