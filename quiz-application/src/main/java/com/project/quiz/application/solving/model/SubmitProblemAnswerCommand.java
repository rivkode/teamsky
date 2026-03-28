package com.project.quiz.application.solving.model;

import com.project.quiz.domain.problem.ProblemAnswerFormat;

import java.util.List;

public record SubmitProblemAnswerCommand(
        Long problemId,
        Long userId,
        ProblemAnswerFormat answerType,
        List<Integer> selectedChoices,
        String subjectiveAnswer
) {
}
