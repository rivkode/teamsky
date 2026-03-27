package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.solving.UserChapterSolvingState;

public interface LoadUserChapterSolvingStatePort {

    UserChapterSolvingState load(Long userId, Long chapterId);
}
