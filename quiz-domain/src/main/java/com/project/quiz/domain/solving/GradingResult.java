package com.project.quiz.domain.solving;

import com.project.quiz.domain.problem.ProblemAnswerFormat;

import java.util.List;

public record GradingResult(
        Long problemId,
        ProblemAnswerFormat answerFormat,
        AnswerStatus answerStatus,
        String explanation,
        List<String> problemAnswers
) {
}
