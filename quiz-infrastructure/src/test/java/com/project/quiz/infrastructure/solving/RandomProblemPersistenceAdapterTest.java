package com.project.quiz.infrastructure.solving;

import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerKey;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SolvedProblem;
import com.project.quiz.domain.solving.SubmittedAnswer;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import com.project.quiz.infrastructure.TestInfrastructureApplication;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.ChapterJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemAnswerKeyJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.mapper.ProblemMapper;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemAnswerKeyJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptAnswerJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ChapterRepositoryImpl.class, ProblemRepositoryImpl.class, SolveAttemptRepositoryImpl.class, ProblemMapper.class})
@ContextConfiguration(classes = TestInfrastructureApplication.class)
class SolvingRepositoryIntegrationTest {

    @Autowired
    private ChapterRepositoryImpl chapterRepository;

    @Autowired
    private ProblemRepositoryImpl problemRepository;

    @Autowired
    private SolveAttemptRepositoryImpl solveAttemptRepository;

    @Autowired
    private ChapterJpaRepository chapterJpaRepository;

    @Autowired
    private ProblemJpaRepository problemJpaRepository;

    @Autowired
    private ProblemChoiceJpaRepository problemChoiceJpaRepository;

    @Autowired
    private ProblemAnswerKeyJpaRepository problemAnswerKeyJpaRepository;

    @Autowired
    private SolveAttemptJpaRepository solveAttemptJpaRepository;

    @Autowired
    private SolveAttemptAnswerJpaRepository solveAttemptAnswerJpaRepository;

    @BeforeEach
    void setUp() {
        solveAttemptAnswerJpaRepository.deleteAll();
        solveAttemptJpaRepository.deleteAll();
        problemAnswerKeyJpaRepository.deleteAll();
        problemChoiceJpaRepository.deleteAll();
        problemJpaRepository.deleteAll();
        chapterJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("chapter 문제와 선택지를 domain problem으로 조합해 조회한다")
    void loadProblemsByChapterId() {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.saveAll(List.of(
                ProblemJpaEntity.create(100L, 1L, "problem-1", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "explanation-1"),
                ProblemJpaEntity.create(101L, 1L, "problem-2", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER, "explanation-2")
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(100L, 1, "p1-choice-1"),
                ProblemChoiceJpaEntity.create(100L, 2, "p1-choice-2"),
                ProblemChoiceJpaEntity.create(101L, 1, "p2-choice-1"),
                ProblemChoiceJpaEntity.create(101L, 2, "p2-choice-2")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(100L, 2),
                ProblemAnswerKeyJpaEntity.objective(101L, 1),
                ProblemAnswerKeyJpaEntity.objective(101L, 2)
        ));

        List<Problem> problems = problemRepository.findAllByChapterId(1L);

        assertThat(problems).hasSize(2);
        assertThat(problems.get(0).id()).isEqualTo(100L);
        assertThat(problems.get(0).chapterId()).isEqualTo(1L);
        assertThat(problems.get(0).answerFormat()).isEqualTo(ProblemAnswerFormat.OBJECTIVE);
        assertThat(problems.get(0).choices()).extracting(choice -> choice.content())
                .containsExactly("p1-choice-1", "p1-choice-2");
        assertThat(problems.get(0).answerKey()).isEqualTo(new ProblemAnswerKey(Set.of(2), List.of()));
        assertThat(problems.get(1).type()).isEqualTo(ProblemType.MULTIPLE_ANSWER);
    }

    @Test
    @DisplayName("사용자 챕터 풀이 상태를 solved 목록과 마지막 skipped 문제로 조회한다")
    void loadUserChapterSolvingState() {
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusMinutes(3)),
                SolveAttemptJpaEntity.create(1L, 1L, 101L, AttemptStatus.SKIPPED, null, null, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(1L, 1L, 102L, AttemptStatus.SKIPPED, null, null, LocalDateTime.now().minusMinutes(1))
        ));

        UserChapterSolvingState state = solveAttemptRepository.findUserChapterSolvingState(1L, 1L);

        assertThat(state.userId()).isEqualTo(1L);
        assertThat(state.chapterId()).isEqualTo(1L);
        assertThat(state.solvedProblemIds()).isEqualTo(Set.of(100L));
        assertThat(state.lastSkippedProblemId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("문제 정답률 집계를 조회한다")
    void loadProblemCorrectRateSummary() {
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusMinutes(3)),
                SolveAttemptJpaEntity.create(2L, 1L, 100L, AttemptStatus.SOLVED, false, AnswerStatus.INCORRECT, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(3L, 1L, 100L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusMinutes(1)),
                SolveAttemptJpaEntity.create(3L, 1L, 100L, AttemptStatus.SKIPPED, null, null, LocalDateTime.now())
        ));

        Optional<ProblemCorrectRateSummary> summary = solveAttemptRepository.findCorrectRateByProblemId(100L);

        assertThat(summary).isPresent();
        assertThat(summary.get().solvedUserCount()).isEqualTo(3L);
        assertThat(summary.get().correctUserCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("chapter 존재 여부를 확인한다")
    void existsByChapterId() {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));

        assertThat(chapterRepository.existsById(1L)).isTrue();
        assertThat(chapterRepository.existsById(99L)).isFalse();
    }

    @Test
    @DisplayName("문제 상세를 조회하면 정답 키와 해설을 함께 반환한다")
    void loadProblemDetailById() {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.save(
                ProblemJpaEntity.create(200L, 1L, "subjective-problem", ProblemAnswerFormat.SUBJECTIVE, ProblemType.SINGLE_ANSWER, "subjective-explanation")
        );
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.subjective(200L, "싱글톤"),
                ProblemAnswerKeyJpaEntity.subjective(200L, "singleton")
        ));

        Optional<Problem> loaded = problemRepository.findById(200L);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().answerFormat()).isEqualTo(ProblemAnswerFormat.SUBJECTIVE);
        assertThat(loaded.get().explanation()).isEqualTo("subjective-explanation");
        assertThat(loaded.get().answerKey().subjectiveAnswers()).containsExactly("싱글톤", "singleton");
    }

    @Test
    @DisplayName("객관식 제출을 저장하면 풀이 이력과 선택 답안이 함께 저장된다")
    void saveObjectiveSolvedAttempt() {
        solveAttemptRepository.saveSolvedAttempt(
                1L,
                1L,
                300L,
                new SubmittedAnswer(ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null),
                AnswerStatus.PARTIAL
        );

        List<SolveAttemptJpaEntity> attempts = solveAttemptJpaRepository.findAll();
        List<SolveAttemptAnswerJpaEntity> answers = solveAttemptAnswerJpaRepository.findAll();

        assertThat(attempts).hasSize(1);
        assertThat(attempts.get(0).getUserId()).isEqualTo(1L);
        assertThat(attempts.get(0).getChapterId()).isEqualTo(1L);
        assertThat(attempts.get(0).getProblemId()).isEqualTo(300L);
        assertThat(attempts.get(0).getStatus()).isEqualTo(AttemptStatus.SOLVED);
        assertThat(attempts.get(0).getCorrect()).isFalse();
        assertThat(attempts.get(0).getAnswerStatus()).isEqualTo(AnswerStatus.PARTIAL);
        assertThat(answers).hasSize(2);
        assertThat(answers).extracting(SolveAttemptAnswerJpaEntity::getAnswerFormat)
                .containsOnly(ProblemAnswerFormat.OBJECTIVE);
        assertThat(answers).extracting(SolveAttemptAnswerJpaEntity::getChoiceSequence)
                .containsExactlyInAnyOrder(1, 3);
    }

    @Test
    @DisplayName("주관식 제출을 저장하면 텍스트 답안이 함께 저장된다")
    void saveSubjectiveSolvedAttempt() {
        solveAttemptRepository.saveSolvedAttempt(
                2L,
                2L,
                400L,
                new SubmittedAnswer(ProblemAnswerFormat.SUBJECTIVE, null, "싱글톤"),
                AnswerStatus.CORRECT
        );

        List<SolveAttemptJpaEntity> attempts = solveAttemptJpaRepository.findAll();
        List<SolveAttemptAnswerJpaEntity> answers = solveAttemptAnswerJpaRepository.findAll();

        assertThat(attempts).hasSize(1);
        assertThat(attempts.get(0).getCorrect()).isTrue();
        assertThat(attempts.get(0).getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(answers).hasSize(1);
        assertThat(answers.get(0).getAnswerFormat()).isEqualTo(ProblemAnswerFormat.SUBJECTIVE);
        assertThat(answers.get(0).getSubjectiveAnswer()).isEqualTo("싱글톤");
    }

    @Test
    @DisplayName("사용자와 문제 기준 최신 풀이 상세를 조회한다")
    void findLatestSolvedProblem() {
        SolveAttemptJpaEntity olderAttempt = solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(1L, 1L, 500L, AttemptStatus.SOLVED, false, AnswerStatus.INCORRECT, LocalDateTime.now().minusMinutes(2))
        );
        solveAttemptAnswerJpaRepository.save(
                SolveAttemptAnswerJpaEntity.objective(olderAttempt.getId(), 3)
        );

        SolveAttemptJpaEntity latestAttempt = solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(1L, 1L, 500L, AttemptStatus.SOLVED, false, AnswerStatus.PARTIAL, LocalDateTime.now().minusMinutes(1))
        );
        solveAttemptAnswerJpaRepository.saveAll(List.of(
                SolveAttemptAnswerJpaEntity.objective(latestAttempt.getId(), 1),
                SolveAttemptAnswerJpaEntity.objective(latestAttempt.getId(), 3)
        ));

        Optional<SolvedProblem> solvedProblem = solveAttemptRepository.findLatestSolvedProblem(1L, 500L);

        assertThat(solvedProblem).isPresent();
        assertThat(solvedProblem.get().answerStatus()).isEqualTo(AnswerStatus.PARTIAL);
        assertThat(solvedProblem.get().userAnswer().selectedChoices()).containsExactly(1, 3);
    }
}
