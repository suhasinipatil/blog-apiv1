package com.example.blogapi.articles;

import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
import com.example.blogapi.comments.CommentEntity;
import com.example.blogapi.comments.CommentRepository;
import com.example.blogapi.comments.CommentService;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    private final UserRepository UserRepository;

    private final CommentService commentService;
    private final ModelMapper modelMapper;

    public ArticlesService(ArticlesRepository articlesRepository, UserRepository userRepository, CommentService commentService, ModelMapper modelMapper) {
        this.articlesRepository = articlesRepository;
        this.UserRepository = userRepository;
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    public List<ResponseArticleDTO> getAllArticlesWithComments() {
        List<ArticleEntity> articleEntities = articlesRepository.findAll();
        // Sort the articles in descending order of creation date
        articleEntities = articleEntities.stream()
                .sorted(Comparator.comparing(ArticleEntity::getCreatedAt).reversed())
                .collect(Collectors.toList());

        List<ResponseArticleDTO> articleDTOList = new ArrayList<>();

        for(ArticleEntity articleEntity: articleEntities){
            ResponseArticleDTO responseArticleDTO = modelMapper.map(articleEntity, ResponseArticleDTO.class);
            responseArticleDTO.setAuthor(articleEntity.getAuthor().getUsername());
            responseArticleDTO.setAuthorId(articleEntity.getAuthor().getId());
            List<ResponseCommentDTO> responseCommentDTOList = new ArrayList<>();
            for(CommentEntity commentEntity: articleEntity.getCommentEntityList()){
                ResponseCommentDTO responseCommentDTO = modelMapper.map(commentEntity, ResponseCommentDTO.class);
                responseCommentDTOList.add(responseCommentDTO);
            }
            responseArticleDTO.setCommentEntities(responseCommentDTOList);
            articleDTOList.add(responseArticleDTO);
        }
        return articleDTOList;
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

    public ArticleEntity getArticleById(Integer id){
        var articleEntity = articlesRepository.findById(id);
        if(articleEntity.isPresent()){
            return articleEntity.get();
        }
        else{
            throw new IllegalArgumentException("Article not found for the id");
        }
    }

    public ArticleEntity updateArticle(Integer id, CreateArticleDTO articleEntity, Integer userId){
        var savedArticle = articlesRepository.findById(id);
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
