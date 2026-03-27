package com.project.quiz.application.solving.port.out;

public interface SaveSkippedProblemPort {

    void saveSkippedProblem(Long userId, Long chapterId, Long problemId);
}
