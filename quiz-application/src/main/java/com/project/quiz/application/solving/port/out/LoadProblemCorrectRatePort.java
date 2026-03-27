package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;

import java.util.Optional;

public interface LoadProblemCorrectRatePort {

    Optional<ProblemCorrectRateSummary> loadByProblemId(Long problemId);
}
