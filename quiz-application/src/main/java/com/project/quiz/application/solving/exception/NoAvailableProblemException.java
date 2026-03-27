package com.project.quiz.application.solving.exception;

public class NoAvailableProblemException extends RuntimeException {

    public NoAvailableProblemException(Long userId, Long chapterId) {
        super("No available problem for userId=%d, chapterId=%d".formatted(userId, chapterId));
    }
}
