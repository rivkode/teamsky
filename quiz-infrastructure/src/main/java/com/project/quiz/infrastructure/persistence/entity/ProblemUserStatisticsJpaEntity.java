package com.project.quiz.infrastructure.persistence.entity;

import com.project.quiz.domain.solving.AnswerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "problem_user_statistics",
        uniqueConstraints = @UniqueConstraint(name = "uk_problem_user_statistics", columnNames = {"problem_id", "user_id"})
)
public class ProblemUserStatisticsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "latest_answer_status", nullable = false, length = 20)
    private AnswerStatus latestAnswerStatus;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "counted_as_solved", nullable = false)
    private boolean countedAsSolved;

    @Column(name = "counted_as_correct", nullable = false)
    private boolean countedAsCorrect;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ProblemUserStatisticsJpaEntity() {
    }

    public static ProblemUserStatisticsJpaEntity firstSolved(Long problemId, Long userId, AnswerStatus answerStatus, LocalDateTime now) {
        ProblemUserStatisticsJpaEntity entity = new ProblemUserStatisticsJpaEntity();
        entity.problemId = problemId;
        entity.userId = userId;
        entity.latestAnswerStatus = answerStatus;
        entity.correct = answerStatus == AnswerStatus.CORRECT;
        entity.countedAsSolved = true;
        entity.countedAsCorrect = answerStatus == AnswerStatus.CORRECT;
        entity.createdAt = now;
        entity.updatedAt = now;
        return entity;
    }

    public void updateLatestAnswerStatus(AnswerStatus answerStatus, LocalDateTime now) {
        this.latestAnswerStatus = answerStatus;
        this.correct = answerStatus == AnswerStatus.CORRECT;
        this.updatedAt = now;
    }

    public Long getProblemId() {
        return problemId;
    }

    public Long getUserId() {
        return userId;
    }
}
