package com.project.quiz.application.statistics.repository;

import com.project.quiz.domain.statistics.ProblemStatistics;

import java.util.Optional;

public interface ProblemStatisticsCommandRepository {

    Optional<ProblemStatistics> findStatistics(Long problemId);

    Optional<ProblemStatistics> findStatisticsForUpdate(Long problemId);

    void save(ProblemStatistics problemStatistics);
}
