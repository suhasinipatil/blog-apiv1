package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.articles.ArticlesRepository;
import com.example.blogapi.articles.ArticlesService;
import com.example.blogapi.comments.dto.CreateCommentDTO;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;
    private final UserService userService;

    private final ArticlesRepository articlesRepository;
    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper, UserService userService, ArticlesRepository articlesRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.articlesRepository = articlesRepository;
    }

    public CommentEntity createComment(CreateCommentDTO createCommentDTO, ArticleEntity article, Integer userId){
        UserEntity userEntity = userService.findById(userId);
        if(userEntity == null){
            throw new IllegalArgumentException("User not found");
        }

        var commentEntity = modelMapper.map(createCommentDTO, CommentEntity.class);
        commentEntity.setAuthor(userEntity);

        var savedCommentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> commentEntityList = article.getCommentEntityList();

        if(commentEntityList == null)
            commentEntityList = new ArrayList<>();

        commentEntityList.add(savedCommentEntity);
        article.setCommentEntityList(commentEntityList);
        articlesRepository.save(article);
        return savedCommentEntity;
    }

    public ResponseCommentDTO deleteComment(Integer commentId, Integer userId){
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if(commentEntity.isEmpty()){
            throw new IllegalArgumentException("Comment not present");
        }
        else{
            if(commentEntity.get().getAuthor().getId().intValue() != userId){
                throw new IllegalArgumentException("User not authorized to delete this comment");
            }
            else{
                commentRepository.deleteById(commentId);
                return modelMapper.map(commentEntity.get(), ResponseCommentDTO.class);
            }
        }
    }

}
