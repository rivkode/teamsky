package com.project.quiz.application.solving.repository;

import com.project.quiz.domain.problem.Problem;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository {

    boolean existsByIdAndChapterId(Long problemId, Long chapterId);

    List<Problem> findAllByChapterId(Long chapterId);

    Optional<Problem> findById(Long problemId);

    void lockById(Long problemId);
}
