package com.project.quiz.domain.problem;

import java.util.List;
import java.util.Set;

public record ProblemAnswerKey(
        Set<Integer> objectiveAnswers,
        List<String> subjectiveAnswers
) {
}
