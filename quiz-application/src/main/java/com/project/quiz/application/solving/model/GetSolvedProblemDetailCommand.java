package com.project.quiz.application.solving.model;

public record GetSolvedProblemDetailCommand(
        Long userId,
        Long problemId
) {
}
