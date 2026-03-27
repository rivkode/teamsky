package com.project.quiz.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chapters")
public class ChapterJpaEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    protected ChapterJpaEntity() {
    }

    public static ChapterJpaEntity create(Long id, String title) {
        ChapterJpaEntity entity = new ChapterJpaEntity();
        entity.id = id;
        entity.title = title;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
