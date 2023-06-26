package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
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
    private final ArticlesService articlesService;

    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper, UserService userService, ArticlesService articlesService) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.articlesService = articlesService;
    }

    public List<ResponseCommentDTO> getAllComments(String slug){
        Optional<List<CommentEntity>> commentEntityList = commentRepository.findBySlug(slug);
        List<ResponseCommentDTO> responseCommentDTOList = new ArrayList<>();
        for(CommentEntity commentEntity: commentEntityList.get()){
            responseCommentDTOList.add(modelMapper.map(commentEntity, ResponseCommentDTO.class));
        }
        return responseCommentDTOList;
    }

    public CommentEntity createComment(CreateCommentDTO createCommentDTO, String slug, Integer userId){
        ArticleEntity article = articlesService.getArticleBySlug(slug);
        var commentEntity = modelMapper.map(createCommentDTO, CommentEntity.class);
        UserEntity userEntity = userService.findById(userId);
        commentEntity.setArticleEntity(article);
        commentEntity.setAuthor(userEntity);
        var savedCommentEntity = commentRepository.save(commentEntity);
        return savedCommentEntity;
    }

    public ResponseCommentDTO deleteComment(Integer commentId, Integer userId){
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentId);
        if(commentEntity.isPresent()){
            if(commentEntity.get().getAuthor().getId() == userId){
                commentRepository.deleteById(commentId);
                return modelMapper.map(commentEntity.get(), ResponseCommentDTO.class);
            }
            else{
                throw new IllegalArgumentException("User not authorized to delete this comment");
            }
        }
        else{
            throw new IllegalArgumentException("Comment not present");
        }
    }
}
