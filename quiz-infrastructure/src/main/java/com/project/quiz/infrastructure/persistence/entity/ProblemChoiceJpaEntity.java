package com.project.quiz.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_choices")
public class ProblemChoiceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    protected ProblemChoiceJpaEntity() {
    }

    public static ProblemChoiceJpaEntity create(Long problemId, Integer sequence, String content) {
        ProblemChoiceJpaEntity entity = new ProblemChoiceJpaEntity();
        entity.problemId = problemId;
        entity.sequence = sequence;
        entity.content = content;
        return entity;
    }

    public Long getProblemId() {
        return problemId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public String getContent() {
        return content;
    }
}
