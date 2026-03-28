package com.project.quiz.application.solving.model;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;

import java.util.List;

public record GetSolvedProblemDetailResult(
        Long problemId,
        ProblemAnswerFormat answerType,
        AnswerStatus answerStatus,
        String explanation,
        List<String> problemAnswers,
        List<String> userAnswers,
        Integer answerCorrectRate
) {
}
