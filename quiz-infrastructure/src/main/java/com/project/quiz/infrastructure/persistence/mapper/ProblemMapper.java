package com.project.quiz.infrastructure.persistence.mapper;

import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerKey;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.infrastructure.persistence.entity.ProblemAnswerKeyJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProblemMapper {

    public Problem toDomain(
            ProblemJpaEntity entity,
            List<ProblemChoiceJpaEntity> choices,
            List<ProblemAnswerKeyJpaEntity> answerKeys
    ) {
        return new Problem(
                entity.getId(),
                entity.getChapterId(),
                entity.getContent(),
                entity.getAnswerFormat(),
                entity.getType(),
                choices.stream()
                        .map(choice -> new ProblemChoice(choice.getSequence(), choice.getContent()))
                        .toList(),
                toAnswerKey(entity.getAnswerFormat(), answerKeys),
                entity.getExplanation()
        );
    }

    private ProblemAnswerKey toAnswerKey(
            ProblemAnswerFormat answerFormat,
            List<ProblemAnswerKeyJpaEntity> answerKeys
    ) {
        if (answerFormat == ProblemAnswerFormat.OBJECTIVE) {
            Set<Integer> objectiveAnswers = answerKeys.stream()
                    .map(ProblemAnswerKeyJpaEntity::getChoiceSequence)
                    .collect(Collectors.toSet());
            return new ProblemAnswerKey(objectiveAnswers, List.of());
        }

        List<String> subjectiveAnswers = answerKeys.stream()
                .map(ProblemAnswerKeyJpaEntity::getSubjectiveAnswer)
                .toList();
        return new ProblemAnswerKey(Set.of(), subjectiveAnswers);
    }
}
