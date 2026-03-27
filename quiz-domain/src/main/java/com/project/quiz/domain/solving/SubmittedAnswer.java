package com.project.quiz.domain.solving;

import com.project.quiz.domain.problem.ProblemAnswerFormat;

import java.util.List;

public record SubmittedAnswer(
        ProblemAnswerFormat answerFormat,
        List<Integer> selectedChoices,
        String subjectiveAnswer
) {
}
