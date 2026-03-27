package com.project.quiz.domain.statistics;

public final class ProblemCorrectRatePolicy {

    private static final long MINIMUM_SOLVED_USER_COUNT = 30L;

    public Integer calculateExposedRate(ProblemCorrectRateSummary summary) {
        if (summary.solvedUserCount() < MINIMUM_SOLVED_USER_COUNT) {
            return null;
        }

        double rate = ((double) summary.correctUserCount() / summary.solvedUserCount()) * 100;
        return (int) Math.round(rate);
    }
}
