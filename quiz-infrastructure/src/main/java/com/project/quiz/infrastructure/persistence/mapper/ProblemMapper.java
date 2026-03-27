package com.project.quiz.infrastructure.persistence.mapper;

import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProblemMapper {

    public Problem toDomain(ProblemJpaEntity entity, List<ProblemChoiceJpaEntity> choices) {
        return new Problem(
                entity.getId(),
                entity.getChapterId(),
                entity.getContent(),
                entity.getType(),
                choices.stream()
                        .map(choice -> new ProblemChoice(choice.getSequence(), choice.getContent()))
                        .toList()
        );
    }
}
