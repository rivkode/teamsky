package com.project.quiz.application.solving.exception;

public class ProblemAnswerTypeMismatchException extends RuntimeException {

    public ProblemAnswerTypeMismatchException(Long problemId) {
        super("Problem answer type mismatch. problemId=" + problemId);
    }
}
