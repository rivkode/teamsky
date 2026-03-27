package com.project.quiz.application.solving.port.in;

public record SkipProblemCommand(Long userId, Long chapterId, Long problemId) {
}
