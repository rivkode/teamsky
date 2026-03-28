package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolveAttemptJpaRepository extends JpaRepository<SolveAttemptJpaEntity, Long> {

    @Query("""
            select sa.problemId
            from SolveAttemptJpaEntity sa
            where sa.userId = :userId
              and sa.chapterId = :chapterId
              and sa.status = com.project.quiz.infrastructure.persistence.entity.AttemptStatus.SOLVED
            """)
    List<Long> findSolvedProblemIds(@Param("userId") Long userId, @Param("chapterId") Long chapterId);

    Optional<SolveAttemptJpaEntity> findTopByUserIdAndChapterIdAndStatusOrderByIdDesc(
            Long userId,
            Long chapterId,
            AttemptStatus status
    );

    Optional<SolveAttemptJpaEntity> findTopByUserIdAndProblemIdAndStatusOrderByIdDesc(
            Long userId,
            Long problemId,
            AttemptStatus status
    );

    @Query("""
            select
                count(distinct sa.userId) as solvedUserCount,
                count(distinct case when sa.correct = true then sa.userId end) as correctUserCount
            from SolveAttemptJpaEntity sa
            where sa.problemId = :problemId
              and sa.status = com.project.quiz.infrastructure.persistence.entity.AttemptStatus.SOLVED
            """)
    Optional<ProblemCorrectRateProjection> findCorrectRateSummaryByProblemId(@Param("problemId") Long problemId);
}
