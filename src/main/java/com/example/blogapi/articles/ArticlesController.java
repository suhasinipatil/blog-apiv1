package com.example.blogapi.articles;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
import com.example.blogapi.comments.CommentEntity;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import org.modelmapper.ModelMapper;
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
     * Endpoint for getting all articles.
     *
     * @return a list of all articles
     */
    @GetMapping("")
    public ResponseEntity<List<ResponseArticleDTO>> getArticles(){
        return ResponseEntity.ok(articlesService.getAllArticlesWithComments());
    }

    /**
     * Endpoint for getting articles by author name.
     *
     * @param authorName the name of the author
     * @return a list of articles by the specified author
     */
    @GetMapping("/author")
    public ResponseEntity<List<ResponseArticleDTO>> getArticlesByAuthorName(@RequestParam(required = false) String authorName){
        if (authorName == null || authorName.isEmpty()) {
            return getArticles();
        }
        List<ArticleEntity> articleEntities = articlesService.getArticlesByAuthorName(authorName);
        List<ResponseArticleDTO> responseArticleDTOList = new ArrayList<>();
        for(ArticleEntity article: articleEntities){
            ResponseArticleDTO articleDTO = modelMapper.map(article, ResponseArticleDTO.class);
            articleDTO.setAuthor(article.getAuthor().getUsername());
            responseArticleDTOList.add(articleDTO);
        }
        return ResponseEntity.ok(responseArticleDTOList);
    }

    /**
     * Endpoint for getting private articles.
     *
     * @param userId the ID of the user
     * @return a string message indicating the private articles for the user
     */
    @GetMapping("/private")
    public String getPrivateArticles(@AuthenticationPrincipal Integer userId){
        return "Private Articles fetched for = " + userId;
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
            throw new IllegalArgumentException("User not logged in");
        }
        var savedArticle = articlesService.createArticle(articleEntity, userId);
        var savedResponseArticle = modelMapper.map(savedArticle, ResponseArticleDTO.class);
        savedResponseArticle.setAuthor(savedArticle.getAuthor().getUsername());
        savedResponseArticle.setAuthorId(savedArticle.getAuthor().getId());
        return ResponseEntity.created(new URI("/articles/" + savedArticle.getId())).body(savedResponseArticle);
    }

    /**
     * Endpoint for getting an article by slug.
     *
     * @param id the ID of the article
     * @return the article with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseArticleDTO> getArticleBySlug(@PathVariable Integer id){
        ArticleEntity article = articlesService.getArticleById(id);
        var savedArticle = modelMapper.map(article, ResponseArticleDTO.class);
        savedArticle.setAuthor(article.getAuthor().getUsername());
        savedArticle.setAuthorId(article.getAuthor().getId());

        List<ResponseCommentDTO> responseCommentDTOList = new ArrayList<>();
        for(CommentEntity commentEntity: article.getCommentEntityList()){
            ResponseCommentDTO responseCommentDTO = modelMapper.map(commentEntity, ResponseCommentDTO.class);
            responseCommentDTOList.add(responseCommentDTO);
        }
        savedArticle.setCommentEntities(responseCommentDTOList);
        return ResponseEntity.ok(savedArticle);
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
            throw new IllegalArgumentException("User not logged in");
        }
        var updatedArticle = articlesService.updateArticle(id, articleEntity, userId);
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