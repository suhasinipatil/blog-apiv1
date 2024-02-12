package com.example.blogapi.articles;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
import com.example.blogapi.users.UserController;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling requests related to articles.
 */
@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = "http://localhost:3000")
public class ArticlesController {

    private final ArticlesService articlesService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(ArticlesController.class);
    /**
     * Constructor for ArticlesController.
     *
     * @param articlesService the service for handling operations related to articles
     * @param modelMapper the mapper for converting between DTOs and entities
     */
    public ArticlesController(ArticlesService articlesService, ModelMapper modelMapper) {
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }

    /**
     * Endpoint for getting articles by author name.
     *
     * @param authorName the name of the author
     * @return a list of articles by the specified author
     */
    @GetMapping("")
    public ResponseEntity<List<ResponseArticleDTO>> getArticles(@RequestParam(required = false) String authorName, @RequestParam(required = false) Integer id){
        logger.info("Received request to get articles");
        if(id != null){
            logger.info("Received request to get article by id: {}", id);
            ArticleEntity article = articlesService.getArticleById(id);
            var savedArticle = modelMapper.map(article, ResponseArticleDTO.class);
            savedArticle.setAuthor(article.getAuthor().getUsername());
            articlesService.convertToResponseArticleDTO(article, savedArticle, modelMapper);
            return ResponseEntity.ok(List.of(savedArticle));
        }
        else if (authorName == null || authorName.isEmpty()) {
            logger.info("Received request to get all articles");
            return ResponseEntity.ok(articlesService.getAllArticlesWithComments());
        }
        else {
            logger.info("Received request to get articles by author: {}", authorName);
            List<ArticleEntity> articleEntities = articlesService.getArticlesByAuthorName(authorName);
            List<ResponseArticleDTO> responseArticleDTOList = new ArrayList<>();
            for(ArticleEntity article : articleEntities){
                ResponseArticleDTO articleDTO = modelMapper.map(article, ResponseArticleDTO.class);
                articleDTO.setAuthor(article.getAuthor().getUsername());
                responseArticleDTOList.add(articleDTO);
            }
            return ResponseEntity.ok(responseArticleDTOList);
        }
    }

    /**
     * Endpoint for creating a new article.
     *
     * @param articleEntity the article to be created
     * @param userId the ID of the user
     * @return the created article
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    @PostMapping("")
    public ResponseEntity<ResponseArticleDTO> createArticle(@RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId) throws URISyntaxException {
        if(userId == null){
            logger.warn("User not logged in with id: {}", userId);
            throw new IllegalArgumentException("User not logged in");
        }
        logger.info("Received request to create article");
        var savedArticle = articlesService.createArticle(articleEntity, userId);
        var savedResponseArticle = modelMapper.map(savedArticle, ResponseArticleDTO.class);
        savedResponseArticle.setAuthor(savedArticle.getAuthor().getUsername());
        savedResponseArticle.setAuthorId(savedArticle.getAuthor().getId());
        logger.info("Article created with id: {}", savedArticle.getId());
        return ResponseEntity.created(new URI("/articles/" + savedArticle.getId())).body(savedResponseArticle);
    }

    /**
     * Endpoint for updating an article.
     *
     * @param id the ID of the article
     * @param articleEntity the updated article
     * @param userId the ID of the user
     * @return the updated article
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseArticleDTO> updateArticle(@PathVariable Integer id, @RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId){
        if(userId == null){
            logger.warn("User not logged in with id: {}", userId);
            throw new IllegalArgumentException("User not logged in");
        }
        logger.info("Received request to update article with id: {}", id);
        var updatedArticle = articlesService.updateArticle(id, articleEntity, userId);
        logger.info("Article with id: {} updated", id);
        return ResponseEntity.ok(modelMapper.map(updatedArticle, ResponseArticleDTO.class));
    }

    /**
     * Exception handler for IllegalArgumentException.
     *
     * @param ex the exception
     * @return a response entity with a bad request status and the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Exception handler for JWTDecodeException.
     *
     * @param ex the exception
     * @return a response entity with an unauthorized status and the exception message
     */
    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<String> handleJWTDecodeException(JWTDecodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT decoding failed: " + ex.getMessage());
    }
}