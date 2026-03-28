package com.project.quiz.application.solving.exception;

public class SolvedProblemNotFoundException extends RuntimeException {

    public SolvedProblemNotFoundException(Long userId, Long problemId) {
        super("Solved problem not found. userId=%d, problemId=%d".formatted(userId, problemId));
    }
}
