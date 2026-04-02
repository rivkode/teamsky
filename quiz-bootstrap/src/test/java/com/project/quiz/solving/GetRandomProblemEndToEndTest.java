package com.project.quiz.solving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.application.solving.model.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.service.SubmitProblemAnswerService;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.ChapterJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemAnswerKeyJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemStatisticsJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemUserStatisticsJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.repository.ProblemAnswerKeyJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemStatisticsJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemUserStatisticsJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptAnswerJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.SolveAttemptJpaRepository;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.AnswerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.project.quiz.QuizBootstrapApplication.class)
@AutoConfigureMockMvc
class GetRandomProblemEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubmitProblemAnswerService submitProblemAnswerService;

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

    @Autowired
    private ProblemStatisticsJpaRepository problemStatisticsJpaRepository;

    @Autowired
    private ProblemUserStatisticsJpaRepository problemUserStatisticsJpaRepository;

    @BeforeEach
    void setUp() {
        problemUserStatisticsJpaRepository.deleteAll();
        problemStatisticsJpaRepository.deleteAll();
        solveAttemptAnswerJpaRepository.deleteAll();
        solveAttemptJpaRepository.deleteAll();
        problemAnswerKeyJpaRepository.deleteAll();
        problemChoiceJpaRepository.deleteAll();
        problemJpaRepository.deleteAll();
        chapterJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("실제 포트 구현이 연결된 상태에서 랜덤 문제를 조회한다")
    void getRandomProblemEndToEnd() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.saveAll(List.of(
                ProblemJpaEntity.create(100L, 1L, "이미 푼 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설"),
                ProblemJpaEntity.create(101L, 1L, "직전 스킵 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설"),
                ProblemJpaEntity.create(102L, 1L, "출제될 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER, "해설")
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(100L, 1, "100-1"),
                ProblemChoiceJpaEntity.create(100L, 2, "100-2"),
                ProblemChoiceJpaEntity.create(101L, 1, "101-1"),
                ProblemChoiceJpaEntity.create(101L, 2, "101-2"),
                ProblemChoiceJpaEntity.create(102L, 1, "102-1"),
                ProblemChoiceJpaEntity.create(102L, 2, "102-2"),
                ProblemChoiceJpaEntity.create(102L, 3, "102-3"),
                ProblemChoiceJpaEntity.create(102L, 4, "102-4"),
                ProblemChoiceJpaEntity.create(102L, 5, "102-5")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(100L, 1),
                ProblemAnswerKeyJpaEntity.objective(101L, 1),
                ProblemAnswerKeyJpaEntity.objective(102L, 2),
                ProblemAnswerKeyJpaEntity.objective(102L, 4)
        ));
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(1L, 1L, 101L, AttemptStatus.SKIPPED, null, null, LocalDateTime.now().minusMinutes(1)),
                SolveAttemptJpaEntity.create(2L, 1L, 102L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusSeconds(50)),
                SolveAttemptJpaEntity.create(3L, 1L, 102L, AttemptStatus.SOLVED, false, AnswerStatus.INCORRECT, LocalDateTime.now().minusSeconds(40)),
                SolveAttemptJpaEntity.create(4L, 1L, 102L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusSeconds(30))
        ));
        problemStatisticsJpaRepository.save(
                ProblemStatisticsJpaEntity.create(102L, 3L, 2L, null, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "chapterId", 1L,
                                "userId", 1L
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(102))
                .andExpect(jsonPath("$.data.content").value("출제될 문제"))
                .andExpect(jsonPath("$.data.choices[0]").value("102-1"))
                .andExpect(jsonPath("$.data.choices[4]").value("102-5"))
                .andExpect(jsonPath("$.data.answerCorrectRate").value(nullValue()))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("모든 문제가 이미 풀었거나 직전 스킵이면 409를 반환한다")
    void returnConflictWhenNoAvailableProblemExistsEndToEnd() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.saveAll(List.of(
                ProblemJpaEntity.create(100L, 1L, "이미 푼 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설"),
                ProblemJpaEntity.create(101L, 1L, "직전 스킵 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설")
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(100L, 1, "100-1"),
                ProblemChoiceJpaEntity.create(101L, 1, "101-1")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(100L, 1),
                ProblemAnswerKeyJpaEntity.objective(101L, 1)
        ));
        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(1L, 1L, 100L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusMinutes(2)),
                SolveAttemptJpaEntity.create(1L, 1L, 101L, AttemptStatus.SKIPPED, null, null, LocalDateTime.now().minusMinutes(1))
        ));

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "chapterId", 1L,
                                "userId", 1L
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NO_AVAILABLE_PROBLEM"));
    }

    @Test
    @DisplayName("문제 넘기기를 호출하면 skip 기록 후 다음 문제를 반환한다")
    void skipProblemAndReturnNextRandomProblemEndToEnd() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.saveAll(List.of(
                ProblemJpaEntity.create(1001L, 1L, "현재 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설"),
                ProblemJpaEntity.create(1002L, 1L, "다음 문제", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "해설")
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(1001L, 1, "1001-1"),
                ProblemChoiceJpaEntity.create(1002L, 1, "1002-1"),
                ProblemChoiceJpaEntity.create(1002L, 2, "1002-2"),
                ProblemChoiceJpaEntity.create(1002L, 3, "1002-3"),
                ProblemChoiceJpaEntity.create(1002L, 4, "1002-4"),
                ProblemChoiceJpaEntity.create(1002L, 5, "1002-5")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(1001L, 1),
                ProblemAnswerKeyJpaEntity.objective(1002L, 2)
        ));

        mockMvc.perform(post("/api/problems/skip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "chapterId", 1L,
                                "userId", 1L,
                                "problemId", 1001L
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(1002))
                .andExpect(jsonPath("$.data.content").value("다음 문제"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("문제 제출 시 채점 결과와 해설을 반환한다")
    void submitProblemAnswerEndToEnd() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.save(ProblemJpaEntity.create(
                3001L, 1L, "정답을 모두 고르세요", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER, "정답은 1번과 2번입니다."
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(3001L, 1, "선택지 1"),
                ProblemChoiceJpaEntity.create(3001L, 2, "선택지 2"),
                ProblemChoiceJpaEntity.create(3001L, 3, "선택지 3"),
                ProblemChoiceJpaEntity.create(3001L, 4, "선택지 4"),
                ProblemChoiceJpaEntity.create(3001L, 5, "선택지 5")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(3001L, 1),
                ProblemAnswerKeyJpaEntity.objective(3001L, 2)
        ));
        problemStatisticsJpaRepository.save(
                ProblemStatisticsJpaEntity.create(3001L, 0L, 0L, null, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "problemId", 3001L,
                                "userId", 1L,
                                "answerType", "OBJECTIVE",
                                "selectedChoices", List.of(1, 3)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(3001))
                .andExpect(jsonPath("$.data.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.data.explanation").value("정답은 1번과 2번입니다."))
                .andExpect(jsonPath("$.data.problemAnswers[0]").value("1"))
                .andExpect(jsonPath("$.data.problemAnswers[1]").value("2"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("풀었던 문제 상세를 조회한다")
    void getSolvedProblemDetailEndToEnd() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.save(ProblemJpaEntity.create(
                4001L, 1L, "정답을 모두 고르세요", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER, "정답은 1번과 2번입니다."
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(4001L, 1, "선택지 1"),
                ProblemChoiceJpaEntity.create(4001L, 2, "선택지 2"),
                ProblemChoiceJpaEntity.create(4001L, 3, "선택지 3"),
                ProblemChoiceJpaEntity.create(4001L, 4, "선택지 4"),
                ProblemChoiceJpaEntity.create(4001L, 5, "선택지 5")
        ));
        problemAnswerKeyJpaRepository.saveAll(List.of(
                ProblemAnswerKeyJpaEntity.objective(4001L, 1),
                ProblemAnswerKeyJpaEntity.objective(4001L, 2)
        ));

        SolveAttemptJpaEntity solvedAttempt = solveAttemptJpaRepository.save(
                SolveAttemptJpaEntity.create(1L, 1L, 4001L, AttemptStatus.SOLVED, false, AnswerStatus.PARTIAL, LocalDateTime.now())
        );
        solveAttemptAnswerJpaRepository.saveAll(List.of(
                com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity.objective(solvedAttempt.getId(), 1),
                com.project.quiz.infrastructure.persistence.entity.SolveAttemptAnswerJpaEntity.objective(solvedAttempt.getId(), 3)
        ));

        solveAttemptJpaRepository.saveAll(List.of(
                SolveAttemptJpaEntity.create(2L, 1L, 4001L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusSeconds(30)),
                SolveAttemptJpaEntity.create(3L, 1L, 4001L, AttemptStatus.SOLVED, false, AnswerStatus.INCORRECT, LocalDateTime.now().minusSeconds(20)),
                SolveAttemptJpaEntity.create(4L, 1L, 4001L, AttemptStatus.SOLVED, true, AnswerStatus.CORRECT, LocalDateTime.now().minusSeconds(10))
        ));
        problemStatisticsJpaRepository.save(
                ProblemStatisticsJpaEntity.create(4001L, 4L, 2L, null, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/problems/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", 1L,
                                "problemId", 4001L
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(4001))
                .andExpect(jsonPath("$.data.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.data.problemAnswers[0]").value("1"))
                .andExpect(jsonPath("$.data.problemAnswers[1]").value("2"))
                .andExpect(jsonPath("$.data.userAnswers[0]").value("1"))
                .andExpect(jsonPath("$.data.userAnswers[1]").value("3"))
                .andExpect(jsonPath("$.data.answerCorrectRate").value(nullValue()))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("문제 제출 시 최초 풀이 사용자 기준으로 문제 통계를 갱신한다")
    void updateProblemStatisticsWhenSubmitSucceeds() throws Exception {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.save(ProblemJpaEntity.create(
                5001L, 1L, "정답을 고르세요", ProblemAnswerFormat.OBJECTIVE, ProblemType.SINGLE_ANSWER, "정답은 2번입니다."
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(5001L, 1, "선택지 1"),
                ProblemChoiceJpaEntity.create(5001L, 2, "선택지 2"),
                ProblemChoiceJpaEntity.create(5001L, 3, "선택지 3"),
                ProblemChoiceJpaEntity.create(5001L, 4, "선택지 4"),
                ProblemChoiceJpaEntity.create(5001L, 5, "선택지 5")
        ));
        problemAnswerKeyJpaRepository.save(
                ProblemAnswerKeyJpaEntity.objective(5001L, 2)
        );
        problemStatisticsJpaRepository.save(
                ProblemStatisticsJpaEntity.create(5001L, 0L, 0L, null, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "problemId", 5001L,
                                "userId", 1L,
                                "answerType", "OBJECTIVE",
                                "selectedChoices", List.of(2)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answerStatus").value("CORRECT"))
                .andExpect(jsonPath("$.error").isEmpty());

        ProblemStatisticsJpaEntity statistics = problemStatisticsJpaRepository.findById(5001L).orElseThrow();

        assert statistics.getSolvedUserCount() == 1L;
        assert statistics.getCorrectUserCount() == 1L;
        assert statistics.getCorrectRate() == null;
    }

    @Test
    @DisplayName("동일한 문제에 대해 서로 다른 100명의 사용자가 동시에 제출해도 문제 통계가 정확히 집계된다")
    void concurrentSubmitByDifferentUsers() throws Exception {
        long problemId = 6001L;
        prepareObjectiveProblem(problemId, ProblemType.SINGLE_ANSWER);

        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch readyLatch = new CountDownLatch(userCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        ConcurrentLinkedQueue<Throwable> failures = new ConcurrentLinkedQueue<>();
        List<Future<?>> futures = new ArrayList<>();

        for (int index = 0; index < userCount; index++) {
            long userId = 10_000L + index;
            futures.add(executorService.submit(() -> {
                readyLatch.countDown();
                await(startLatch, failures);
                try {
                    submitProblemAnswerService.submit(new SubmitProblemAnswerCommand(
                            problemId,
                            userId,
                            ProblemAnswerFormat.OBJECTIVE,
                            List.of(2),
                            null
                    ));
                } catch (Throwable throwable) {
                    failures.add(throwable);
                }
            }));
        }

        assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

        assertThat(failures).isEmpty();

        ProblemStatisticsJpaEntity statistics = problemStatisticsJpaRepository.findById(problemId).orElseThrow();
        long attemptCount = solveAttemptJpaRepository.findAll().stream()
                .filter(attempt -> attempt.getProblemId().equals(problemId))
                .count();
        long userStatisticsCount = problemUserStatisticsJpaRepository.findAll().stream()
                .filter(stat -> stat.getProblemId().equals(problemId))
                .count();

        assertThat(attemptCount).isEqualTo(100L);
        assertThat(userStatisticsCount).isEqualTo(100L);
        assertThat(statistics.getSolvedUserCount()).isEqualTo(100L);
        assertThat(statistics.getCorrectUserCount()).isEqualTo(100L);
        assertThat(statistics.getCorrectRate()).isEqualTo(100);
    }

    @Test
    @DisplayName("동일한 사용자가 같은 문제를 동시에 여러 번 제출해도 distinct user 기준 통계는 1번만 집계된다")
    void concurrentDuplicateSubmitBySameUser() throws Exception {
        long problemId = 6002L;
        long userId = 20_001L;
        prepareObjectiveProblem(problemId, ProblemType.SINGLE_ANSWER);

        int requestCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch readyLatch = new CountDownLatch(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        ConcurrentLinkedQueue<Throwable> failures = new ConcurrentLinkedQueue<>();
        List<Future<?>> futures = new ArrayList<>();

        for (int index = 0; index < requestCount; index++) {
            futures.add(executorService.submit(() -> {
                readyLatch.countDown();
                await(startLatch, failures);
                try {
                    submitProblemAnswerService.submit(new SubmitProblemAnswerCommand(
                            problemId,
                            userId,
                            ProblemAnswerFormat.OBJECTIVE,
                            List.of(2),
                            null
                    ));
                } catch (Throwable throwable) {
                    failures.add(throwable);
                }
            }));
        }

        assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

        assertThat(failures).isEmpty();

        ProblemStatisticsJpaEntity statistics = problemStatisticsJpaRepository.findById(problemId).orElseThrow();
        long attemptCount = solveAttemptJpaRepository.findAll().stream()
                .filter(attempt -> attempt.getProblemId().equals(problemId))
                .count();
        long userStatisticsCount = problemUserStatisticsJpaRepository.findAll().stream()
                .filter(stat -> stat.getProblemId().equals(problemId) && stat.getUserId().equals(userId))
                .count();

        assertThat(attemptCount).isEqualTo(100L);
        assertThat(userStatisticsCount).isEqualTo(1L);
        assertThat(statistics.getSolvedUserCount()).isEqualTo(1L);
        assertThat(statistics.getCorrectUserCount()).isEqualTo(1L);
        assertThat(statistics.getCorrectRate()).isNull();
    }

    private void prepareObjectiveProblem(long problemId, ProblemType problemType) {
        chapterJpaRepository.save(ChapterJpaEntity.create(1L, "chapter-1"));
        problemJpaRepository.save(ProblemJpaEntity.create(
                problemId,
                1L,
                "동시성 검증 문제",
                ProblemAnswerFormat.OBJECTIVE,
                problemType,
                "정답은 2번입니다."
        ));
        problemChoiceJpaRepository.saveAll(List.of(
                ProblemChoiceJpaEntity.create(problemId, 1, "선택지 1"),
                ProblemChoiceJpaEntity.create(problemId, 2, "선택지 2"),
                ProblemChoiceJpaEntity.create(problemId, 3, "선택지 3"),
                ProblemChoiceJpaEntity.create(problemId, 4, "선택지 4"),
                ProblemChoiceJpaEntity.create(problemId, 5, "선택지 5")
        ));
        problemAnswerKeyJpaRepository.save(
                ProblemAnswerKeyJpaEntity.objective(problemId, 2)
        );
        problemStatisticsJpaRepository.save(
                ProblemStatisticsJpaEntity.create(problemId, 0L, 0L, null, LocalDateTime.now())
        );
    }

    private void await(CountDownLatch latch, ConcurrentLinkedQueue<Throwable> failures) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            failures.add(exception);
        }
    }
}
