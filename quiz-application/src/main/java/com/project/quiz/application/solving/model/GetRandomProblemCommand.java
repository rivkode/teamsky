package com.project.quiz.application.solving.model;

public record GetRandomProblemCommand(
        Long userId,
        Long chapterId
) {
}
