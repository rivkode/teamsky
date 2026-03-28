package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemUserStatisticsRepository;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.infrastructure.persistence.entity.ProblemUserStatisticsJpaEntity;
import com.project.quiz.infrastructure.persistence.repository.ProblemUserStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemUserStatisticsRepositoryImpl implements ProblemUserStatisticsRepository {

    private final ProblemUserStatisticsJpaRepository problemUserStatisticsJpaRepository;

    @Override
    @Transactional
    public boolean trySaveFirstSolved(Long problemId, Long userId, AnswerStatus answerStatus) {
        int insertedRows = problemUserStatisticsJpaRepository.insertIgnoreFirstSolved(
                problemId,
                userId,
                answerStatus.name(),
                answerStatus == AnswerStatus.CORRECT,
                LocalDateTime.now()
        );
        return insertedRows > 0;
    }

    @Override
    @Transactional
    public void updateLatestAnswerStatus(Long problemId, Long userId, AnswerStatus answerStatus) {
        problemUserStatisticsJpaRepository.findByProblemIdAndUserId(problemId, userId)
                .ifPresent(entity -> entity.updateLatestAnswerStatus(answerStatus, LocalDateTime.now()));
    }
}
