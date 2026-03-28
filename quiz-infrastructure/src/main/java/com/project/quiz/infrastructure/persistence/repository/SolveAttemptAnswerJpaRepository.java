package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolveAttemptAnswerJpaRepository extends JpaRepository<SolveAttemptAnswerJpaEntity, Long> {

    java.util.List<SolveAttemptAnswerJpaEntity> findAllBySolveAttemptIdOrderByIdAsc(Long solveAttemptId);
}
