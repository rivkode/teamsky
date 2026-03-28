package com.project.quiz.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_statistics")
public class ProblemStatisticsJpaEntity {

    @Id
    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "solved_user_count", nullable = false)
    private long solvedUserCount;

    @Column(name = "correct_user_count", nullable = false)
    private long correctUserCount;

    @Column(name = "correct_rate")
    private Integer correctRate;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ProblemStatisticsJpaEntity() {
    }

    public static ProblemStatisticsJpaEntity create(Long problemId, long solvedUserCount, long correctUserCount, Integer correctRate, LocalDateTime updatedAt) {
        ProblemStatisticsJpaEntity entity = new ProblemStatisticsJpaEntity();
        entity.problemId = problemId;
        entity.solvedUserCount = solvedUserCount;
        entity.correctUserCount = correctUserCount;
        entity.correctRate = correctRate;
        entity.updatedAt = updatedAt;
        return entity;
    }

    public void update(long solvedUserCount, long correctUserCount, Integer correctRate, LocalDateTime updatedAt) {
        this.solvedUserCount = solvedUserCount;
        this.correctUserCount = correctUserCount;
        this.correctRate = correctRate;
        this.updatedAt = updatedAt;
    }

    public Long getProblemId() {
        return problemId;
    }

    public long getSolvedUserCount() {
        return solvedUserCount;
    }

    public long getCorrectUserCount() {
        return correctUserCount;
    }

    public Integer getCorrectRate() {
        return correctRate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
