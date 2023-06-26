package com.example.blogapi.articles;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.articles.dto.ResponseArticleDTO;
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
public class ArticlesController {

    private final ArticlesService articlesService;

    private final ModelMapper modelMapper;
    public ArticlesController(ArticlesService articlesService, ModelMapper modelMapper) {
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<ArticleEntity>> getArticles(){
        return ResponseEntity.ok(articlesService.getAllArticles());
    }

    @GetMapping("?authorName={authorName}")
    public ResponseEntity<List<ResponseArticleDTO>> getArticlesByAuthorName(@PathVariable String authorName){
        List<ArticleEntity> articleEntities = articlesService.getArticlesByAuthorName(authorName);
        List<ResponseArticleDTO> responseArticleDTOList = new ArrayList<>();
        for(ArticleEntity article: articleEntities){
            responseArticleDTOList.add(modelMapper.map(article, ResponseArticleDTO.class));
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
        return ResponseEntity.created(new URI("/articles/" + savedArticle.getId())).body(savedResponseArticle);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseArticleDTO> getArticleBySlug(@PathVariable String slug){
        ArticleEntity article = articlesService.getArticleBySlug(slug);
        var savedArticle = modelMapper.map(article, ResponseArticleDTO.class);
        return ResponseEntity.ok(savedArticle);
    }

    @PatchMapping("/{slug}")
    public ResponseEntity<ResponseArticleDTO> updateArticle(@PathVariable String slug, @RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId){
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        var updatedArticle = articlesService.updateArticle(slug, articleEntity, userId);
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
