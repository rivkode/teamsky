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

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_status", length = 20)
    private com.project.quiz.domain.solving.AnswerStatus answerStatus;

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
            com.project.quiz.domain.solving.AnswerStatus answerStatus,
            LocalDateTime createdAt
    ) {
        SolveAttemptJpaEntity entity = new SolveAttemptJpaEntity();
        entity.userId = userId;
        entity.chapterId = chapterId;
        entity.problemId = problemId;
        entity.status = status;
        entity.correct = correct;
        entity.answerStatus = answerStatus;
        entity.createdAt = createdAt;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public com.project.quiz.domain.solving.AnswerStatus getAnswerStatus() {
        return answerStatus;
    }
}
