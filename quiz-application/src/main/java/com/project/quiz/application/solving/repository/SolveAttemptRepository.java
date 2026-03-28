package com.project.quiz.application.solving.repository;

import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SolvedProblem;
import com.project.quiz.domain.solving.SubmittedAnswer;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;

import java.util.Optional;

public interface SolveAttemptRepository {

    UserChapterSolvingState findUserChapterSolvingState(Long userId, Long chapterId);

    Optional<ProblemCorrectRateSummary> findCorrectRateByProblemId(Long problemId);

    Optional<SolvedProblem> findLatestSolvedProblem(Long userId, Long problemId);

    void saveSkippedProblem(Long userId, Long chapterId, Long problemId);

    void saveSolvedAttempt(Long userId, Long chapterId, Long problemId, SubmittedAnswer answer, AnswerStatus answerStatus);
}
