package com.project.quiz.application.solving.model;

import java.util.List;

public record GetRandomProblemResult(
        Long problemId,
        String content,
        List<GetRandomProblemChoiceResult> choices,
        Integer answerCorrectRate
) {
}
