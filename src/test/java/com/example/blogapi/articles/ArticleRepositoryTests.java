package com.example.blogapi.articles;

import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ArticleRepositoryTests {

    @Autowired
    private ArticlesRepository articlesRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByAuthorId(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("cde");
        userEntity.setEmail("cde@gmail.com");
        userEntity.setPassword("cde");
        userRepository.save(userEntity);

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setAuthor(userEntity);
        articleEntity.setTitle("cde");
        articleEntity.setBody("cde");
        articleEntity.setSlug("cde");
        articlesRepository.save(articleEntity);

        var article = articlesRepository.findByAuthorId(1);
        assertNotNull(article);
    }
}
