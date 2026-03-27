package com.project.quiz.application.solving.exception;

public class ChapterNotFoundException extends RuntimeException {

    public ChapterNotFoundException(Long chapterId) {
        super("Chapter not found. chapterId=" + chapterId);
    }
}
