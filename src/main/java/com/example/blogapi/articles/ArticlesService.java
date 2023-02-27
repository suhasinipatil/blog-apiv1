package com.example.blogapi.articles;

import org.springframework.stereotype.Service;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    public ArticlesService(ArticlesRepository articlesRepository) {
        this.articlesRepository = articlesRepository;
    }
}
