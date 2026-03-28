package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.ProblemUserStatisticsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProblemUserStatisticsJpaRepository extends JpaRepository<ProblemUserStatisticsJpaEntity, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO problem_user_statistics
            (problem_id, user_id, latest_answer_status, is_correct, counted_as_solved, counted_as_correct, created_at, updated_at)
            VALUES
            (:problemId, :userId, :answerStatus, :correct, true, :correct, :now, :now)
            ON DUPLICATE KEY UPDATE
            latest_answer_status = latest_answer_status
            """, nativeQuery = true)
    int insertIgnoreFirstSolved(
            @Param("problemId") Long problemId,
            @Param("userId") Long userId,
            @Param("answerStatus") String answerStatus,
            @Param("correct") boolean correct,
            @Param("now") java.time.LocalDateTime now
    );

    Optional<ProblemUserStatisticsJpaEntity> findByProblemIdAndUserId(Long problemId, Long userId);
}
