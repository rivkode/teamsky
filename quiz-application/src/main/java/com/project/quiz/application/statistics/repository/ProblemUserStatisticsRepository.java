package com.project.quiz.application.statistics.repository;

import com.project.quiz.domain.solving.AnswerStatus;

public interface ProblemUserStatisticsRepository {

    boolean trySaveFirstSolved(Long problemId, Long userId, AnswerStatus answerStatus);

    void updateLatestAnswerStatus(Long problemId, Long userId, AnswerStatus answerStatus);
}
