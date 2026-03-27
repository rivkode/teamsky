package com.project.quiz.solving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.infrastructure.persistence.entity.AttemptStatus;
import com.project.quiz.infrastructure.persistence.entity.ChapterJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemAnswerKeyJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemChoiceJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.ProblemJpaEntity;
import com.project.quiz.infrastructure.persistence.entity.SolveAttemptJpaEntity;
import com.project.quiz.infrastructure.persistence.repository.ProblemAnswerKeyJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemChoiceJpaRepository;
import com.project.quiz.infrastructure.persistence.repository.ProblemJpaRepository;
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
import java.util.List;
import java.util.Map;

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

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "chapterId", 1L,
                                "userId", 1L
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(102))
                .andExpect(jsonPath("$.content").value("출제될 문제"))
                .andExpect(jsonPath("$.choices[0]").value("102-1"))
                .andExpect(jsonPath("$.choices[4]").value("102-5"))
                .andExpect(jsonPath("$.answerCorrectRate").value(nullValue()));
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
                .andExpect(jsonPath("$.code").value("NO_AVAILABLE_PROBLEM"));
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
                .andExpect(jsonPath("$.problemId").value(1002))
                .andExpect(jsonPath("$.content").value("다음 문제"));
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

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "problemId", 3001L,
                                "userId", 1L,
                                "answerType", "OBJECTIVE",
                                "selectedChoices", List.of(1, 3)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(3001))
                .andExpect(jsonPath("$.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.explanation").value("정답은 1번과 2번입니다."))
                .andExpect(jsonPath("$.problemAnswers[0]").value("1"))
                .andExpect(jsonPath("$.problemAnswers[1]").value("2"));
    }
}
