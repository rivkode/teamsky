package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SubmittedAnswer;

public interface SaveSolvedAttemptPort {

    void saveSolvedAttempt(Long userId, Long chapterId, Long problemId, SubmittedAnswer answer, AnswerStatus answerStatus);
}
