package com.example.blogapi.articles;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface ArticlesRepository extends JpaRepository<ArticleEntity, Integer> {

    Optional<List<ArticleEntity>> findByAuthorId(Integer authorId);

    Optional<ArticleEntity> findBySlug(String slug);


}
