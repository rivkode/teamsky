package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemJpaRepository extends JpaRepository<ProblemJpaEntity, Long> {

    List<ProblemJpaEntity> findAllByChapterIdOrderByIdAsc(@Param("chapterId") Long chapterId);

    boolean existsByIdAndChapterId(Long id, Long chapterId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select p
            from ProblemJpaEntity p
            where p.id = :problemId
            """)
    Optional<ProblemJpaEntity> findByIdForUpdate(@Param("problemId") Long problemId);
}
