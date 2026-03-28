package com.project.quiz.domain.solving;

public record SolvedProblem(
        Long problemId,
        AnswerStatus answerStatus,
        SubmittedAnswer userAnswer
) {
}
