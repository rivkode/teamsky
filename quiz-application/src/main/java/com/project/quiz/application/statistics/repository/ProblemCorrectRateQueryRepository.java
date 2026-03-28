package com.project.quiz.application.statistics.repository;

import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;

import java.util.Optional;

public interface ProblemCorrectRateQueryRepository {

    Optional<ProblemCorrectRateSummary> findCorrectRateSummary(Long problemId);
}
