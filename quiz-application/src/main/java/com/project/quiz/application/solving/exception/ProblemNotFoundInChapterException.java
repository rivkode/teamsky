package com.project.quiz.application.solving.exception;

public class ProblemNotFoundInChapterException extends RuntimeException {

    public ProblemNotFoundInChapterException(Long chapterId, Long problemId) {
        super("Problem not found in chapter. chapterId=%d, problemId=%d".formatted(chapterId, problemId));
    }
}
