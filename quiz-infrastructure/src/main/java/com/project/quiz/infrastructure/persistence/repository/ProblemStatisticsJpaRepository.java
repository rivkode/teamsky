package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.ProblemStatisticsJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProblemStatisticsJpaRepository extends JpaRepository<ProblemStatisticsJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ps
            from ProblemStatisticsJpaEntity ps
            where ps.problemId = :problemId
            """)
    Optional<ProblemStatisticsJpaEntity> findByProblemIdForUpdate(@Param("problemId") Long problemId);
}
