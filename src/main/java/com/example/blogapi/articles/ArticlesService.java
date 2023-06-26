package com.example.blogapi.articles;

import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    private final UserRepository UserRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    public ArticlesService(ArticlesRepository articlesRepository, UserRepository userRepository, ModelMapper modelMapper, UserService userService) {
        this.articlesRepository = articlesRepository;
        this.UserRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public List<ArticleEntity> getAllArticles() {
        return articlesRepository.findAll();
    }

    public ArticleEntity createArticle(CreateArticleDTO articleEntity, Integer userId) {
        var userEntity = UserRepository.findById(userId);
        var toBeSavedArticle = modelMapper.map(articleEntity, ArticleEntity.class);
        if(userEntity.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        toBeSavedArticle.setAuthor(userEntity.get());
        return articlesRepository.save(toBeSavedArticle);
    }

    public List<ArticleEntity> getArticlesByAuthorName(String authorName){
        var userEntity = UserRepository.findByUsername(authorName);
        Optional<List<ArticleEntity>> lsOfArticleEntity = articlesRepository.findByAuthorId(userEntity.getId());
        if(lsOfArticleEntity.isPresent()){
            return lsOfArticleEntity.get().stream().toList();
        }
        else{
            throw new IllegalArgumentException("No Articles found");
        }
    }

    public ArticleEntity getArticleBySlug(String slug){
        var articleEntity = articlesRepository.findBySlug(slug);
        if(articleEntity.isPresent()){
            return articleEntity.get();
        }
        else{
            throw new IllegalArgumentException("Article not found for the slug");
        }
    }

    public ArticleEntity updateArticle(String slug, CreateArticleDTO articleEntity, Integer userId){
        var savedArticle = articlesRepository.findBySlug(slug);
        if(savedArticle.isPresent()){
            var article = savedArticle.get();
            if(article.getAuthor().getId() == userId){
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                modelMapper.map(articleEntity, article);
                return articlesRepository.save(article);
            }
            else {
                throw new IllegalArgumentException("User not authorized to update this article");
            }
        }
        return null;
    }
}
