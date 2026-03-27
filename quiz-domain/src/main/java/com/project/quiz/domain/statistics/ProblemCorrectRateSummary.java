package com.project.quiz.domain.statistics;

public record ProblemCorrectRateSummary(
        long solvedUserCount,
        long correctUserCount
) {
}
