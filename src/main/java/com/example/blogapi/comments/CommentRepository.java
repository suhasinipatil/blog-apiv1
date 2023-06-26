package com.example.blogapi.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    @Query("select cm from comments cm join articles ar on cm.articleEntity.id = ar.id")
    Optional<List<CommentEntity>> findBySlug(String slug);

}
