package com.project.quiz.infrastructure.solving;

import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.mapper.ProblemMapper;
import com.project.quiz.infrastructure.persistence.repository.ProblemAnswerKeyJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepository {

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemChoiceJpaRepository problemChoiceJpaRepository;
    private final ProblemAnswerKeyJpaRepository problemAnswerKeyJpaRepository;
    private final ProblemMapper problemMapper;

    @Override
    public boolean existsByIdAndChapterId(Long problemId, Long chapterId) {
        return problemJpaRepository.existsByIdAndChapterId(problemId, chapterId);
    }

    @Override
    public List<Problem> findAllByChapterId(Long chapterId) {
        List<com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity> problems =
                problemJpaRepository.findAllByChapterIdOrderByIdAsc(chapterId);

        List<Long> problemIds = problems.stream()
                .map(com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity::getId)
                .toList();

        Map<Long, List<ProblemChoiceJpaEntity>> choicesByProblemId = problemChoiceJpaRepository
                .findAllByProblemIdInOrderByProblemIdAscSequenceAsc(problemIds)
                .stream()
                .collect(Collectors.groupingBy(ProblemChoiceJpaEntity::getProblemId));

        return problems.stream()
                .map(problem -> problemMapper.toDomain(
                        problem,
                        choicesByProblemId.getOrDefault(problem.getId(), List.of()),
                        problemAnswerKeyJpaRepository.findAllByProblemIdOrderByIdAsc(problem.getId())
                ))
                .toList();
    }

    @Override
    public Optional<Problem> findById(Long problemId) {
        return problemJpaRepository.findById(problemId)
                .map(problem -> problemMapper.toDomain(
                        problem,
                        problemChoiceJpaRepository.findAllByProblemIdInOrderByProblemIdAscSequenceAsc(List.of(problemId)),
                        problemAnswerKeyJpaRepository.findAllByProblemIdOrderByIdAsc(problemId)
                ));
    }
}
