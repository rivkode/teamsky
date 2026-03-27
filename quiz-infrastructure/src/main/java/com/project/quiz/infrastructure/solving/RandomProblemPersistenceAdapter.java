package com.project.quiz.infrastructure.solving;

import com.project.quiz.application.solving.port.out.CheckChapterExistsPort;
import com.project.quiz.application.solving.port.out.CheckProblemInChapterPort;
import com.project.quiz.application.solving.port.out.LoadChapterProblemsPort;
import com.project.quiz.application.solving.port.out.LoadProblemDetailPort;
import com.project.quiz.application.solving.port.out.LoadProblemCorrectRatePort;
import com.project.quiz.application.solving.port.out.LoadUserChapterSolvingStatePort;
import com.project.quiz.application.solving.port.out.SaveSkippedProblemPort;
import com.project.quiz.application.solving.port.out.SaveSolvedAttemptPort;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SubmittedAnswer;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.ProblemAnswerKeyJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.mapper.ProblemMapper;
import com.project.quiz.infrastructure.persistence.repository.ProblemAnswerKeyJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptAnswerJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RandomProblemPersistenceAdapter implements
        CheckChapterExistsPort,
        CheckProblemInChapterPort,
        LoadChapterProblemsPort,
        LoadProblemDetailPort,
        LoadUserChapterSolvingStatePort,
        LoadProblemCorrectRatePort,
        SaveSkippedProblemPort,
        SaveSolvedAttemptPort {

    private final ChapterJpaRepository chapterJpaRepository;
    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemChoiceJpaRepository problemChoiceJpaRepository;
    private final ProblemAnswerKeyJpaRepository problemAnswerKeyJpaRepository;
    private final SolveAttemptJpaRepository solveAttemptJpaRepository;
    private final SolveAttemptAnswerJpaRepository solveAttemptAnswerJpaRepository;
    private final ProblemMapper problemMapper;

    @Override
    public boolean existsById(Long chapterId) {
        return chapterJpaRepository.existsById(chapterId);
    }

    @Override
    public boolean existsByIdAndChapterId(Long problemId, Long chapterId) {
        return problemJpaRepository.existsByIdAndChapterId(problemId, chapterId);
    }

    @Override
    public List<Problem> loadByChapterId(Long chapterId) {
        List<com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity> problems =
                problemJpaRepository.findAllByChapterIdOrderByIdAsc(chapterId);

        List<Long> problemIds = problems.stream()
                .map(problem -> problem.getId())
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
    public Optional<Problem> loadById(Long problemId) {
        return problemJpaRepository.findById(problemId)
                .map(problem -> problemMapper.toDomain(
                        problem,
                        problemChoiceJpaRepository.findAllByProblemIdInOrderByProblemIdAscSequenceAsc(List.of(problemId)),
                        problemAnswerKeyJpaRepository.findAllByProblemIdOrderByIdAsc(problemId)
                ));
    }

    @Override
    public UserChapterSolvingState load(Long userId, Long chapterId) {
        List<Long> solvedProblemIds = solveAttemptJpaRepository.findSolvedProblemIds(userId, chapterId);
        Long lastSkippedProblemId = solveAttemptJpaRepository
                .findTopByUserIdAndChapterIdAndStatusOrderByIdDesc(userId, chapterId, AttemptStatus.SKIPPED)
                .map(attempt -> attempt.getProblemId())
                .orElse(null);

        return new UserChapterSolvingState(userId, chapterId, new HashSet<>(solvedProblemIds), lastSkippedProblemId);
    }

    @Override
    public Optional<ProblemCorrectRateSummary> loadByProblemId(Long problemId) {
        return solveAttemptJpaRepository.findCorrectRateSummaryByProblemId(problemId)
                .map(projection -> new ProblemCorrectRateSummary(
                        projection.getSolvedUserCount(),
                        projection.getCorrectUserCount()
                ));
    }

    @Override
    @Transactional
    public void saveSkippedProblem(Long userId, Long chapterId, Long problemId) {
        solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(
                        userId,
                        chapterId,
                        problemId,
                        AttemptStatus.SKIPPED,
                        null,
                        null,
                        java.time.LocalDateTime.now()
                )
        );
    }

    @Override
    @Transactional
    public void saveSolvedAttempt(
            Long userId,
            Long chapterId,
            Long problemId,
            SubmittedAnswer answer,
            AnswerStatus answerStatus
    ) {
        SolveAttemptJpaEntity solveAttempt = solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(
                        userId,
                        chapterId,
                        problemId,
                        AttemptStatus.SOLVED,
                        answerStatus == AnswerStatus.CORRECT,
                        answerStatus,
                        java.time.LocalDateTime.now()
                )
        );

        if (answer.answerFormat() == ProblemAnswerFormat.OBJECTIVE) {
            solveAttemptAnswerJpaRepository.saveAll(
                    answer.selectedChoices().stream()
                            .map(choice -> SolveAttemptAnswerJpaEntity.objective(solveAttempt.getId(), choice))
                            .toList()
            );
            return;
        }

        solveAttemptAnswerJpaRepository.save(
                SolveAttemptAnswerJpaEntity.subjective(solveAttempt.getId(), answer.subjectiveAnswer())
        );
    }
}
