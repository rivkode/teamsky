package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemJpaRepository extends JpaRepository<ProblemJpaEntity, Long> {

    List<ProblemJpaEntity> findAllByChapterIdOrderByIdAsc(@Param("chapterId") Long chapterId);

    boolean existsByIdAndChapterId(Long id, Long chapterId);
}
