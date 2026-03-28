package com.project.quiz.application.solving.model;

public record SkipProblemCommand(
        Long userId,
        Long chapterId,
        Long problemId
) {
}
