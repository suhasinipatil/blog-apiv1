package com.example.blogapi.articles;

import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
import com.example.blogapi.comments.CommentEntity;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import com.example.blogapi.users.UserRepository;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ArticlesService {
    private final ArticlesRepository articlesRepository;

    private final UserRepository UserRepository;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(ArticlesService.class);

    public ArticlesService(ArticlesRepository articlesRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.articlesRepository = articlesRepository;
        this.UserRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<ResponseArticleDTO> getAllArticlesWithComments() {
        logger.info("Received request to get all articles");
        List<ArticleEntity> articleEntities = articlesRepository.findAll();
        // Sort the articles in descending order of creation date
        articleEntities = articleEntities.stream()
                .sorted(Comparator.comparing(ArticleEntity::getCreatedAt).reversed())
                .toList();

        List<ResponseArticleDTO> articleDTOList = new ArrayList<>();

        for(ArticleEntity articleEntity: articleEntities){
            ResponseArticleDTO responseArticleDTO = modelMapper.map(articleEntity, ResponseArticleDTO.class);
            responseArticleDTO.setAuthor(articleEntity.getAuthor().getUsername());
            convertToResponseArticleDTO(articleEntity, responseArticleDTO, modelMapper);
            articleDTOList.add(responseArticleDTO);
        }
        logger.info("Returning all articles");
        return articleDTOList;
    }

    void convertToResponseArticleDTO(ArticleEntity articleEntity, ResponseArticleDTO responseArticleDTO, ModelMapper modelMapper){
        responseArticleDTO.setAuthorId(articleEntity.getAuthor().getId());
        List<ResponseCommentDTO> responseCommentDTOList = new ArrayList<>();
        for(CommentEntity commentEntity: articleEntity.getCommentEntityList()){
            ResponseCommentDTO responseCommentDTO = modelMapper.map(commentEntity, ResponseCommentDTO.class);
            responseCommentDTOList.add(responseCommentDTO);
        }
        responseArticleDTO.setCommentEntities(responseCommentDTOList);
    }

    public ArticleEntity createArticle(CreateArticleDTO articleEntity, Integer userId) {
        logger.info("Received request to create article from user with id: {}", userId);
        var userEntity = UserRepository.findById(userId);
        if(userEntity.isEmpty()){
            logger.error("User with id: {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }
        var toBeSavedArticle = modelMapper.map(articleEntity, ArticleEntity.class);
        toBeSavedArticle.setAuthor(userEntity.get());
        return articlesRepository.save(toBeSavedArticle);
    }

    public List<ArticleEntity> getArticlesByAuthorName(String authorName){
        logger.info("Received request to get articles by author: {}", authorName);
        var userEntity = UserRepository.findByUsername(authorName);
        Optional<List<ArticleEntity>> lsOfArticleEntity = articlesRepository.findByAuthorId(userEntity.getId());
        if(lsOfArticleEntity.isEmpty()){
            logger.error("No Articles found");
            throw new IllegalArgumentException("No Articles found");
        }
        else{
            return lsOfArticleEntity.get().stream().toList();
        }
    }

    public ArticleEntity getArticleById(Integer id){
        logger.info("Received request to get article by id: {}", id);
        var articleEntity = articlesRepository.findById(id);
        if(articleEntity.isEmpty()){
            logger.error("Article not found for the id: {}", id);
            throw new IllegalArgumentException("Article not found for the id");
        }
        else{
            return articleEntity.get();
        }
    }

    public ArticleEntity updateArticle(Integer id, CreateArticleDTO articleEntity, Integer userId){
        logger.info("Received request to update article by id: {}", id);
        var savedArticle = articlesRepository.findById(id);
        if(savedArticle.isEmpty()){
            logger.error("Article not found for the id: {}", id);
            throw new IllegalArgumentException("Article not found for the id");
        }
        else{
            if(userId != savedArticle.get().getAuthor().getId().intValue()){
                logger.error("User not authorized to update this article");
                throw new IllegalArgumentException("User not authorized to update this article");
            }
            else {
                logger.info("Updating article with id: {}", id);
                var article = savedArticle.get();
                Condition notNull = context -> context.getSource() != null;
                modelMapper.getConfiguration().setPropertyCondition(notNull);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                modelMapper.map(articleEntity, article);
                return articlesRepository.save(article);
            }
        }
    }

    public void deleteCommentFromArticle(Integer articleId, Integer commentId) {
        var articleEntityOptional = articlesRepository.findById(articleId);
        if(articleEntityOptional.isEmpty()){
            throw new IllegalArgumentException("Article not found for the id");
        }
        var commentList = articleEntityOptional.get().getCommentEntityList();
        var commentOptional = commentList.stream().filter(comment -> comment.getId().equals(commentId)).findFirst();
        if(commentOptional.isEmpty()){
            throw new IllegalArgumentException("Comment not found for the id");
        }
        commentList.remove(commentOptional.get());
        articlesRepository.save(articleEntityOptional.get());
    }

}
