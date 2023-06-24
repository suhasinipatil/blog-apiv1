package com.example.blogapi.articles.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class CreateArticleDTO {
    String slug;
    String title;
    String subtitle;
    String body;
}
