package com.example.blogapi.commons;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    Date createdAt;
}
