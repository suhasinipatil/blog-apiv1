package com.example.blogapi.comments;

import com.example.blogapi.comments.dto.CreateCommentDTO;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/articles/{articleSlug}/comments")
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;
    public CommentController(CommentService commentService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseCommentDTO>> getCommentBySlug(@PathVariable String articleSlug){
        return ResponseEntity.ok(commentService.getAllComments(articleSlug));
    }

    @PostMapping("")
    public ResponseEntity<ResponseCommentDTO> createComment(@RequestBody CreateCommentDTO createCommentDTO, @PathVariable String articleSlug, @AuthenticationPrincipal Integer userId) throws URISyntaxException {
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        CommentEntity savedComment = commentService.createComment(createCommentDTO, articleSlug, userId);
        var responseCommentDTO = modelMapper.map(savedComment, ResponseCommentDTO.class);
        return ResponseEntity.created(new URI("/comments/" + savedComment.getId())).body(responseCommentDTO);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDTO> deleteComment(@PathVariable Integer commentId, @AuthenticationPrincipal Integer userId, @PathVariable String articleSlug){
        if(userId == null){
            throw new IllegalArgumentException("User not logged in");
        }
        var deletedComment = commentService.deleteComment(commentId, userId);
        return ResponseEntity.accepted().body(deletedComment);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
