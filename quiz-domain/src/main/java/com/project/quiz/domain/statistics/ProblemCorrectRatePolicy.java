package com.project.quiz.domain.statistics;

import java.util.Optional;

public final class ProblemCorrectRatePolicy {

    private static final long MINIMUM_SOLVED_USER_COUNT = 30L;

    public Optional<ProblemCorrectRate> calculate(ProblemCorrectRateSummary summary) {
        if (summary.solvedUserCount() < MINIMUM_SOLVED_USER_COUNT) {
            return Optional.empty();
        }

        double rate = ((double) summary.correctUserCount() / summary.solvedUserCount()) * 100;
        return Optional.of(new ProblemCorrectRate((int) Math.round(rate)));
    }
}
