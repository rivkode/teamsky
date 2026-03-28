package com.project.quiz.application.statistics.repository;

import com.project.quiz.domain.statistics.ProblemStatistics;

import java.util.List;

public interface ProblemStatisticsQueryRepository {

    List<ProblemStatistics> findAllStatistics();
}
