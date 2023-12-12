package com.example.blogapi.articles;

import com.example.blogapi.articles.dto.CreateArticleDTO;
import com.example.blogapi.comments.CommentEntity;
import com.example.blogapi.comments.CommentRepository;
import com.example.blogapi.comments.CommentService;
import com.example.blogapi.comments.dto.CreateCommentDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ArticleServiceTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticlesRepository articlesRepository;
    @Autowired
    private AuthTokenRepository authTokenRepository;
    @Autowired
    private CommentRepository commentRepository;
    private ArticlesService articlesService;
    private UserService userService;
    private CommentService commentService;
    private ArticlesService getArticlesService(){
        if(articlesService == null){
           var modelMapper = new ModelMapper();
           articlesService = new ArticlesService(articlesRepository, userRepository, commentService, modelMapper);
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

    private CreateArticleDTO createArticledto(){
        CreateArticleDTO createArticleDTO = new CreateArticleDTO();
        createArticleDTO.setTitle("cde");
        createArticleDTO.setBody("cde");
        return createArticleDTO;
    }

    private CreateCommentDTO createCommentDTO(){
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setBody("cde");
        createCommentDTO.setTitle("cde");
        return createCommentDTO;
    }

    private CommentService getCommentService(){
        if(commentService == null){
            var modelMapper = new ModelMapper();
            commentService = new CommentService(commentRepository, modelMapper, getUserService(), getArticlesService(),articlesRepository);
        }
        return commentService;
    }

    public CommentEntity createComment(ArticleEntity savedArticle, UserResponseDTO userResponseDTO){
        return getCommentService().createComment(createCommentDTO(), savedArticle, userResponseDTO.getId());
    }

    @Test
    public void createArticle(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        assertNotNull(savedArticle);
    }

    @Test
    public void getArticle(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        var commentCreated = createComment(savedArticle, userResponseDTO);
        var article = getArticlesService().getAllArticlesWithComments();
        assertNotNull(article);
        assertEquals(article.size(), 1);
    }
    
    @Test
    public void getArticleByAuthorName(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        var article = getArticlesService().getArticlesByAuthorName(userResponseDTO.getUsername());
        assertNotNull(article);
        assertEquals(article.size(), 1);
    }

    @Test
    public void createArticleThrowsException(){
        var createArticleDTO = createArticledto();
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            getArticlesService().createArticle(createArticleDTO, 0);
        });
    }

    @Test
    public void getArticleById(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        var article = getArticlesService().getArticleById(savedArticle.getId());
        assertNotNull(article);
        assertEquals(article.getId(), savedArticle.getId());
    }

    @Test
    public void updateArticle(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        createArticleDTO.setTitle("abc");
        var article = getArticlesService().updateArticle(savedArticle.getId(), createArticleDTO, userResponseDTO.getId());
        assertNotNull(article);
        assertEquals(article.getTitle(), createArticleDTO.getTitle());
    }

    @Test
    public void updateArticleThrowsException(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        createArticleDTO.setTitle("abc");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            getArticlesService().updateArticle(savedArticle.getId(), createArticleDTO, 0);
        });
    }
}
