package com.project.quiz.domain.problem;

import java.util.List;

public record Problem(
        Long id,
        Long chapterId,
        String content,
        ProblemType type,
        List<ProblemChoice> choices
) {

    public boolean belongsTo(Long targetChapterId) {
        return chapterId.equals(targetChapterId);
    }
}
