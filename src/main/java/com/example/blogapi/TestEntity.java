package com.example.blogapi;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    String name;
    Boolean completed;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
