package com.project.quiz.domain.statistics;

public class ProblemStatistics {

    private final Long problemId;
    private long solvedUserCount;
    private long correctUserCount;
    private Integer correctRate;

    public ProblemStatistics(Long problemId, long solvedUserCount, long correctUserCount, Integer correctRate) {
        this.problemId = problemId;
        this.solvedUserCount = solvedUserCount;
        this.correctUserCount = correctUserCount;
        this.correctRate = correctRate;
    }

    public static ProblemStatistics initialize(Long problemId) {
        return new ProblemStatistics(problemId, 0L, 0L, null);
    }

    public void applyFirstSolved(boolean correct, ProblemCorrectRatePolicy policy) {
        solvedUserCount += 1;
        if (correct) {
            correctUserCount += 1;
        }
        correctRate = policy.calculate(new ProblemCorrectRateSummary(solvedUserCount, correctUserCount))
                .map(ProblemCorrectRate::value)
                .orElse(null);
    }

    public Long problemId() {
        return problemId;
    }

    public long solvedUserCount() {
        return solvedUserCount;
    }

    public long correctUserCount() {
        return correctUserCount;
    }

    public Integer correctRate() {
        return correctRate;
    }
}
