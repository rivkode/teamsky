package com.project.quiz.application.solving.port.out;

public interface CheckProblemInChapterPort {

    boolean existsByIdAndChapterId(Long problemId, Long chapterId);
}
