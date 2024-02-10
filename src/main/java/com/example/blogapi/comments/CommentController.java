package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.articles.ArticlesService;
import com.example.blogapi.comments.dto.CreateCommentDTO;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/articles/{articleId}/comments")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    private final ArticlesService articlesService;
    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, ArticlesService articlesService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    public ResponseEntity<ResponseCommentDTO> createComment(@RequestBody CreateCommentDTO createCommentDTO, @PathVariable Integer articleId, @AuthenticationPrincipal Integer userId) throws URISyntaxException {
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        ArticleEntity article = articlesService.getArticleById(articleId);
        if(article == null){
            throw new IllegalArgumentException("Article not found");
        }
        CommentEntity savedComment = commentService.createComment(createCommentDTO, article, userId);
        var responseCommentDTO = modelMapper.map(savedComment, ResponseCommentDTO.class);
        return ResponseEntity.created(new URI("/comments/" + savedComment.getId())).body(responseCommentDTO);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDTO> deleteComment(@PathVariable Integer commentId, @AuthenticationPrincipal Integer userId, @PathVariable Integer articleId){
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        articlesService.deleteCommentFromArticle(articleId, commentId);
        var deletedComment = commentService.deleteComment(commentId, userId);
        return ResponseEntity.accepted().body(deletedComment);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
