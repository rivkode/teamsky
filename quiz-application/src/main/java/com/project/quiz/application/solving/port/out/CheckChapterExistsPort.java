package com.project.quiz.application.solving.port.out;

public interface CheckChapterExistsPort {

    boolean existsById(Long chapterId);
}
