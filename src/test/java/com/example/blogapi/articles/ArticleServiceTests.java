package com.example.blogapi.articles;

import com.example.blogapi.CommonTests;
import com.example.blogapi.articles.dto.CreateArticleDTO;
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
    private ArticlesService articlesService;
    private UserService userService;
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

    private CreateArticleDTO createArticledto(){
        CreateArticleDTO createArticleDTO = new CreateArticleDTO();
        createArticleDTO.setTitle("cde");
        createArticleDTO.setBody("cde");
        createArticleDTO.setSlug("cde");
        return createArticleDTO;
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
        var article = getArticlesService().getAllArticles();
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
    public void getArticleBySlug(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        var article = getArticlesService().getArticleBySlug(savedArticle.getSlug());
        assertNotNull(article);
        assertEquals(article.getSlug(), savedArticle.getSlug());
    }

    @Test
    public void updateArticle(){
        var userResponseDTO = createUser();
        var createArticleDTO = createArticledto();
        var savedArticle = getArticlesService().createArticle(createArticleDTO, userResponseDTO.getId());
        createArticleDTO.setTitle("abc");
        var article = getArticlesService().updateArticle(savedArticle.getSlug(), createArticleDTO, userResponseDTO.getId());
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
            getArticlesService().updateArticle(savedArticle.getSlug(), createArticleDTO, 0);
        });
    }
}
