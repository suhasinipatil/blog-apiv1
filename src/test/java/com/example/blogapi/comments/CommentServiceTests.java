package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.articles.ArticlesRepository;
import com.example.blogapi.articles.ArticlesService;
import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.comments.dto.CreateCommentDTO;
import com.example.blogapi.comments.dto.ResponseCommentDTO;
import com.example.blogapi.security.authtokens.AuthTokenRepository;
import com.example.blogapi.security.authtokens.AuthTokenService;
import com.example.blogapi.security.jwt.JWTService;
import com.example.blogapi.users.UserRepository;
import com.example.blogapi.users.UserService;
import com.example.blogapi.users.dto.CreateUserDTO;
import com.example.blogapi.users.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentServiceTests {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticlesRepository articlesRepository;
    @Autowired
    private AuthTokenRepository authTokenRepository;
    private ArticlesService articlesService;
    private UserService userService;

    private CommentService commentService;

    private CommentService getCommentService(){
        if(commentService == null){
            var modelMapper = new ModelMapper();
            commentService = new CommentService(commentRepository, modelMapper, getUserService(), getArticlesService());
        }
        return commentService;
    }

    private ArticlesService getArticlesService(){
        if(articlesService == null){
            var modelMapper = new ModelMapper();
            articlesService = new ArticlesService(articlesRepository, userRepository, modelMapper, getUserService());
        }
        return articlesService;
    }

    public UserService getUserService(){
        if(userService == null){
            var modelMapper = new ModelMapper();
            var passwordEncoder = new BCryptPasswordEncoder();
            var jwtService = new JWTService();
            var authTokenService = new AuthTokenService(authTokenRepository);
            userService = new UserService(userRepository, modelMapper, passwordEncoder, jwtService, authTokenService);
        }
        return userService;
    }

    public UserResponseDTO createUser(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("cde@gmail.com");
        createUserDTO.setUsername("cde");
        createUserDTO.setPassword("cde");
        var savedUser = getUserService().createUser(createUserDTO);
        return savedUser;
    }

    private ArticleEntity createArticledto(Integer userId){
        CreateArticleDTO createArticleDTO = new CreateArticleDTO();
        createArticleDTO.setTitle("cde");
        createArticleDTO.setBody("cde");
        createArticleDTO.setSlug("cde");
        ArticleEntity savedArticle = getArticlesService().createArticle(createArticleDTO, userId);
        return savedArticle;
    }

    private CreateCommentDTO createCommentDTO(){
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setBody("cde");
        createCommentDTO.setTitle("cde");
        return createCommentDTO;
    }

    @Test
    public void createComment(){
        UserResponseDTO userResponseDTO = createUser();
        ArticleEntity savedArticle = createArticledto(userResponseDTO.getId());
        var savedComment = getCommentService().createComment(createCommentDTO(), savedArticle.getSlug(), userResponseDTO.getId());
        assertNotNull(savedComment);
    }

    @Test
    public void getComments(){
        UserResponseDTO userResponseDTO = createUser();
        ArticleEntity savedArticle = createArticledto(userResponseDTO.getId());
        var savedComment = getCommentService().createComment(createCommentDTO(), savedArticle.getSlug(), userResponseDTO.getId());
        List<ResponseCommentDTO> responseCommentDTOList = getCommentService().getAllComments();
        assertNotNull(responseCommentDTOList);
        assertEquals(1, responseCommentDTOList.size());
    }

    @Test
    public void deleteComment(){
        UserResponseDTO userResponseDTO = createUser();
        ArticleEntity savedArticle = createArticledto(userResponseDTO.getId());
        var savedComment = getCommentService().createComment(createCommentDTO(), savedArticle.getSlug(), userResponseDTO.getId());
        ResponseCommentDTO deleteComment = getCommentService().deleteComment(savedComment.getId(), userResponseDTO.getId());
        assertNotNull(deleteComment);

        Optional<CommentEntity> commentEntity = commentRepository.findById(savedComment.getId());
        assertTrue(commentEntity.isEmpty());
    }
}
