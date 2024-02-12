package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.articles.ArticlesController;
import com.example.blogapi.articles.ArticlesRepository;
import com.example.blogapi.articles.ArticlesService;
import com.example.blogapi.comments.dto.CreateCommentDTO;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper, UserService userService, ArticlesRepository articlesRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.articlesRepository = articlesRepository;
    }

    public CommentEntity createComment(CreateCommentDTO createCommentDTO, ArticleEntity article, Integer userId){
        UserEntity userEntity = userService.findById(userId);
        if(userEntity == null){
            logger.error("User not found");
            throw new IllegalArgumentException("User not found");
        }

        logger.info("Creating comment from user : {}", userEntity.getUsername());
        var commentEntity = modelMapper.map(createCommentDTO, CommentEntity.class);
        commentEntity.setAuthor(userEntity);

        var savedCommentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> commentEntityList = article.getCommentEntityList();

        if(commentEntityList == null)
            commentEntityList = new ArrayList<>();

        commentEntityList.add(savedCommentEntity);
        article.setCommentEntityList(commentEntityList);
        articlesRepository.save(article);
        logger.info("Comment created successfully");

        return savedCommentEntity;
    }

    public ResponseCommentDTO deleteComment(Integer commentId, Integer userId){
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if(commentEntity.isEmpty()){
            logger.error("Comment not present");
            throw new IllegalArgumentException("Comment not present");
        }
        else{
            if(commentEntity.get().getAuthor().getId().intValue() != userId){
                logger.error("User not authorized to delete this comment");
                throw new IllegalArgumentException("User not authorized to delete this comment");
            }
            else{
                commentRepository.deleteById(commentId);
                logger.info("Comment deleted successfully");
                return modelMapper.map(commentEntity.get(), ResponseCommentDTO.class);
            }
        }
    }

}
