package com.project.quiz.infrastructure.solving;

import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SolvedProblem;
import com.project.quiz.domain.solving.SubmittedAnswer;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptAnswerJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolveAttemptRepositoryImpl implements SolveAttemptRepository {

    private final SolveAttemptJpaRepository solveAttemptJpaRepository;
    private final SolveAttemptAnswerJpaRepository solveAttemptAnswerJpaRepository;

    @Override
    public UserChapterSolvingState findUserChapterSolvingState(Long userId, Long chapterId) {
        List<Long> solvedProblemIds = solveAttemptJpaRepository.findSolvedProblemIds(userId, chapterId);
        Long lastSkippedProblemId = solveAttemptJpaRepository
                .findTopByUserIdAndChapterIdAndStatusOrderByIdDesc(userId, chapterId, AttemptStatus.SKIPPED)
                .map(SolveAttemptJpaEntity::getProblemId)
                .orElse(null);

        return new UserChapterSolvingState(userId, chapterId, new HashSet<>(solvedProblemIds), lastSkippedProblemId);
    }

    @Override
    public Optional<SolvedProblem> findLatestSolvedProblem(Long userId, Long problemId) {
        return solveAttemptJpaRepository.findTopByUserIdAndProblemIdAndStatusOrderByIdDesc(
                        userId,
                        problemId,
                        AttemptStatus.SOLVED
                )
                .map(this::toSolvedProblem);
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
                        LocalDateTime.now()
                )
        );
    }

    @Override
    @Transactional
    public void saveSolvedAttempt(Long userId, Long chapterId, Long problemId, SubmittedAnswer answer, AnswerStatus answerStatus) {
        SolveAttemptJpaEntity solveAttempt = solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(
                        userId,
                        chapterId,
                        problemId,
                        AttemptStatus.SOLVED,
                        answerStatus == AnswerStatus.CORRECT,
                        answerStatus,
                        LocalDateTime.now()
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

    private SolvedProblem toSolvedProblem(SolveAttemptJpaEntity attempt) {
        List<SolveAttemptAnswerJpaEntity> answers = solveAttemptAnswerJpaRepository.findAllBySolveAttemptIdOrderByIdAsc(attempt.getId());
        if (answers.isEmpty()) {
            return new SolvedProblem(
                    attempt.getProblemId(),
                    attempt.getAnswerStatus(),
                    new SubmittedAnswer(ProblemAnswerFormat.OBJECTIVE, List.of(), null)
            );
        }

        ProblemAnswerFormat answerFormat = answers.get(0).getAnswerFormat();
        SubmittedAnswer submittedAnswer = answerFormat == ProblemAnswerFormat.OBJECTIVE
                ? new SubmittedAnswer(
                ProblemAnswerFormat.OBJECTIVE,
                answers.stream().map(SolveAttemptAnswerJpaEntity::getChoiceSequence).toList(),
                null
        )
                : new SubmittedAnswer(
                ProblemAnswerFormat.SUBJECTIVE,
                null,
                answers.get(0).getSubjectiveAnswer()
        );

        return new SolvedProblem(attempt.getProblemId(), attempt.getAnswerStatus(), submittedAnswer);
    }
}
