package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.problem.Problem;

import java.util.List;

public interface LoadChapterProblemsPort {

    List<Problem> loadByChapterId(Long chapterId);
}
