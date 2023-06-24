package com.example.blogapi.articles;

import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.users.UserService;
import com.example.blogapi.users.dto.LoginUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticlesService articlesService;

    public ArticlesController(ArticlesService articlesService) {
        this.articlesService = articlesService;
    }

    @GetMapping("")
    public ResponseEntity<List<ArticleEntity>> getArticles(){
        return ResponseEntity.ok(articlesService.getAllArticles());
    }

    @GetMapping("?authorName={authorName}")
    public ResponseEntity<List<ArticleEntity>> getArticlesByAuthorName(@PathVariable String authorName){
        return ResponseEntity.ok(articlesService.getArticlesByAuthorName(authorName));
    }

    @GetMapping("/private")
    public String getPrivateArticles(@AuthenticationPrincipal Integer userId){
        return "Private Articles fetched for = " + userId;
    }

    @PostMapping("")
    public ResponseEntity<ArticleEntity> createArticle(@RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId) throws URISyntaxException {
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        var savedArticle = articlesService.createArticle(articleEntity, userId);
        return ResponseEntity.created(new URI("/articles/" + savedArticle.getId())).body(savedArticle);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ArticleEntity> getArticleBySlug(@PathVariable String slug){
        return ResponseEntity.ok(articlesService.getArticleBySlug(slug));
    }

    @PatchMapping("/{slug}")
    public ResponseEntity<ArticleEntity> updateArticle(@PathVariable String slug, @RequestBody CreateArticleDTO articleEntity, @AuthenticationPrincipal Integer userId){
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        return ResponseEntity.ok(articlesService.updateArticle(slug, articleEntity, userId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
