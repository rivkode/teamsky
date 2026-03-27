package com.project.quiz.application.solving.exception;

public class ProblemNotFoundInChapterException extends RuntimeException {

    public ProblemNotFoundInChapterException(Long chapterId, Long problemId) {
        super(chapterId == null
                ? "Problem not found. problemId=%d".formatted(problemId)
                : "Problem not found in chapter. chapterId=%d, problemId=%d".formatted(chapterId, problemId));
    }
}
