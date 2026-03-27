package com.project.quiz.infrastructure.persistence.repository;

import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemChoiceJpaRepository extends JpaRepository<ProblemChoiceJpaEntity, Long> {

    List<ProblemChoiceJpaEntity> findAllByProblemIdInOrderByProblemIdAscSequenceAsc(List<Long> problemIds);
}
