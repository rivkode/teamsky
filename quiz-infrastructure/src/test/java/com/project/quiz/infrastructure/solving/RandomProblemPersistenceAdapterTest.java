package com.project.quiz.infrastructure.solving;

import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import com.project.quiz.infrastructure.TestInfrastructureApplication;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.ChapterJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.mapper.ProblemMapper;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
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
@Import({RandomProblemPersistenceAdapter.class, ProblemMapper.class})
@ContextConfiguration(classes = TestInfrastructureApplication.class)
class RandomProblemPersistenceAdapterTest {

    @Autowired
    private RandomProblemPersistenceAdapter adapter;

    @Autowired
    private ChapterJpaRepository chapterJpaRepository;

    @Autowired
    private ProblemJpaRepository problemJpaRepository;

    @Autowired
    private ProblemChoiceJpaRepository problemChoiceJpaRepository;

    @Autowired
    private SolveAttemptJpaRepository solveAttemptJpaRepository;

    @BeforeEach
    void setUp() {
        solveAttemptJpaRepository.deleteAll();
        problemChoiceJpaRepository.deleteAll();
        problemJpaRepository.deleteAll();
        chapterJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("chapter 문제와 선택지를 domain problem으로 조합해 조회한다")
    void loadProblemsByChapterId() {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.saveAll(List.of(
                ProblemJpaEntity.create(100L, 1L, "problem-1", ProblemType.SINGLE_ANSWER),
                ProblemJpaEntity.create(101L, 1L, "problem-2", ProblemType.MULTIPLE_ANSWER)
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(100L, 1, "p1-choice-1"),
                ProblemChoiceJpaEntity.create(100L, 2, "p1-choice-2"),
                ProblemChoiceJpaEntity.create(101L, 1, "p2-choice-1"),
                ProblemChoiceJpaEntity.create(101L, 2, "p2-choice-2")
        ));

        List<Problem> problems = adapter.loadByChapterId(1L);

        assertThat(problems).hasSize(2);
        assertThat(problems.get(0).id()).isEqualTo(100L);
        assertThat(problems.get(0).chapterId()).isEqualTo(1L);
        assertThat(problems.get(0).choices()).extracting(choice -> choice.content())
                .containsExactly("p1-choice-1", "p1-choice-2");
        assertThat(problems.get(1).type()).isEqualTo(ProblemType.MULTIPLE_ANSWER);
    }

    @Test
    @DisplayName("사용자 챕터 풀이 상태를 solved 목록과 마지막 skipped 문제로 조회한다")
    void loadUserChapterSolvingState() {
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, LocalDateTime.now().minusMinutes(3)),
                SolveAttemptJpaEntity.create(1L, 1L, 101L, AttemptStatus.SKIPPED, null, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(1L, 1L, 102L, AttemptStatus.SKIPPED, null, LocalDateTime.now().minusMinutes(1))
        ));

        UserChapterSolvingState state = adapter.load(1L, 1L);

        assertThat(state.userId()).isEqualTo(1L);
        assertThat(state.chapterId()).isEqualTo(1L);
        assertThat(state.solvedProblemIds()).isEqualTo(Set.of(100L));
        assertThat(state.lastSkippedProblemId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("문제 정답률 집계를 조회한다")
    void loadProblemCorrectRateSummary() {
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, LocalDateTime.now().minusMinutes(3)),
                SolveAttemptJpaEntity.create(2L, 1L, 100L, AttemptStatus.SOLVED, false, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(3L, 1L, 100L, AttemptStatus.SOLVED, true, LocalDateTime.now().minusMinutes(1)),
                SolveAttemptJpaEntity.create(3L, 1L, 100L, AttemptStatus.SKIPPED, null, LocalDateTime.now())
        ));

        Optional<ProblemCorrectRateSummary> summary = adapter.loadByProblemId(100L);

        assertThat(summary).isPresent();
        assertThat(summary.get().solvedUserCount()).isEqualTo(3L);
        assertThat(summary.get().correctUserCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("chapter 존재 여부를 확인한다")
    void existsByChapterId() {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));

        assertThat(adapter.existsById(1L)).isTrue();
        assertThat(adapter.existsById(99L)).isFalse();
    }
}
