package com.project.quiz.application.solving.exception;

import java.util.List;

public class InvalidProblemChoiceException extends RuntimeException {

    public InvalidProblemChoiceException(Long problemId, List<Integer> invalidChoices) {
        super("Invalid choice sequence for problemId=%d: %s".formatted(problemId, invalidChoices));
    }
}
