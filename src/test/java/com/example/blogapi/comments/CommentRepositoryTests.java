package com.example.blogapi.comments;

import com.example.blogapi.articles.ArticleEntity;
import com.example.blogapi.articles.ArticlesRepository;
import com.example.blogapi.users.UserEntity;
import com.example.blogapi.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private ArticlesRepository articlesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void findBySlug(){
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
        articleEntity.setSubtitle("cde");
        articlesRepository.save(articleEntity);

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setBody("cde");
        commentEntity.setTitle("cde");
        commentEntity.setAuthor(userEntity);
        commentRepository.save(commentEntity);

        /*Optional<List<CommentEntity>> commentEntityList = commentRepository.findBySlug(articleEntity.getSlug());
        assertTrue(commentEntityList.isPresent());*/
    }
}
