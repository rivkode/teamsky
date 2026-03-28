package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemStatisticsCommandRepository;
import com.project.quiz.application.statistics.repository.ProblemUserStatisticsRepository;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import com.project.quiz.domain.statistics.ProblemStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemStatisticsUpdateService {

    private final ProblemStatisticsCommandRepository problemStatisticsCommandRepository;
    private final ProblemUserStatisticsRepository problemUserStatisticsRepository;
    private final ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;
    private final ProblemCorrectRatePolicy problemCorrectRatePolicy = new ProblemCorrectRatePolicy();

    @Transactional
    public void recordSolvedProblem(Long problemId, Long userId, AnswerStatus answerStatus) {
        boolean firstSolvedUser = problemUserStatisticsRepository.trySaveFirstSolved(problemId, userId, answerStatus);
        if (!firstSolvedUser) {
            problemUserStatisticsRepository.updateLatestAnswerStatus(problemId, userId, answerStatus);
            return;
        }

        ProblemStatistics problemStatistics = problemStatisticsCommandRepository.findStatisticsForUpdate(problemId)
                .orElseThrow(() -> new IllegalStateException("Problem statistics not found. problemId=" + problemId));

        problemStatistics.applyFirstSolved(answerStatus == AnswerStatus.CORRECT, problemCorrectRatePolicy);
        problemStatisticsCommandRepository.save(problemStatistics);
        problemCorrectRateCacheRepository.put(problemId, problemStatistics.correctRate());
    }
}
