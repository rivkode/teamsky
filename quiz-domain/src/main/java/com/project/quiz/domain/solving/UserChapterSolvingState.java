package com.project.quiz.domain.solving;

import com.project.quiz.domain.problem.Problem;

import java.util.Set;

public record UserChapterSolvingState(
        Long userId,
        Long chapterId,
        Set<Long> solvedProblemIds,
        Long lastSkippedProblemId
) {

    public boolean isSelectable(Problem problem) {
        return problem.belongsTo(chapterId)
                && !isSolved(problem.id())
                && !isLastSkipped(problem.id());
    }

    public boolean isSolved(Long problemId) {
        return solvedProblemIds.contains(problemId);
    }

    public boolean isLastSkipped(Long problemId) {
        return lastSkippedProblemId != null && lastSkippedProblemId.equals(problemId);
    }
}
