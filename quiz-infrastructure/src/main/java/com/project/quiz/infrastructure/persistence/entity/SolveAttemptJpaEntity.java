package com.project.quiz.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "solve_attempts")
public class SolveAttemptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttemptStatus status;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SolveAttemptJpaEntity() {
    }

    public static SolveAttemptJpaEntity create(
            Long userId,
            Long chapterId,
            Long problemId,
            AttemptStatus status,
            Boolean correct,
            LocalDateTime createdAt
    ) {
        SolveAttemptJpaEntity entity = new SolveAttemptJpaEntity();
        entity.userId = userId;
        entity.chapterId = chapterId;
        entity.problemId = problemId;
        entity.status = status;
        entity.correct = correct;
        entity.createdAt = createdAt;
        return entity;
    }

    public Long getProblemId() {
        return problemId;
    }
}
