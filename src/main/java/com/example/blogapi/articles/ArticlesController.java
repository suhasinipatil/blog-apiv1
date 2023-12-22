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

@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = "http://localhost:3000")
public class ArticlesController {

    private final ArticlesService articlesService;

    private final ModelMapper modelMapper;
    public ArticlesController(ArticlesService articlesService, ModelMapper modelMapper) {
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseArticleDTO>> getArticles(){
        return ResponseEntity.ok(articlesService.getAllArticlesWithComments());
    }

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

    @GetMapping("/private")
    public String getPrivateArticles(@AuthenticationPrincipal Integer userId){
        return "Private Articles fetched for = " + userId;
    }

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

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseArticleDTO> updateArticle(@PathVariable Integer id, @RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId){
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        var updatedArticle = articlesService.updateArticle(id, articleEntity, userId);
        return ResponseEntity.ok(modelMapper.map(updatedArticle, ResponseArticleDTO.class));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<String> handleJWTDecodeException(JWTDecodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT decoding failed: " + ex.getMessage());
    }
}
