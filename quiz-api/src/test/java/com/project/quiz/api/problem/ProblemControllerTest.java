package com.project.quiz.api.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.api.TestApiApplication;
import com.project.quiz.api.common.GlobalExceptionHandler;
import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.InvalidProblemChoiceException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.exception.SolvedProblemNotFoundException;
import com.project.quiz.application.solving.model.GetRandomProblemChoiceResult;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailResult;
import com.project.quiz.application.solving.model.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.service.GetRandomProblemService;
import com.project.quiz.application.solving.service.GetSolvedProblemDetailService;
import com.project.quiz.application.solving.service.SkipProblemService;
import com.project.quiz.application.solving.service.SubmitProblemAnswerService;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProblemController.class)
@ContextConfiguration(classes = {
        TestApiApplication.class,
        ProblemController.class,
        GlobalExceptionHandler.class
})
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetRandomProblemService getRandomProblemService;

    @MockBean
    private GetSolvedProblemDetailService getSolvedProblemDetailService;

    @MockBean
    private SkipProblemService skipProblemService;

    @MockBean
    private SubmitProblemAnswerService submitProblemAnswerService;

    @Test
    @DisplayName("랜덤 문제를 정상 반환한다")
    void returnRandomProblem() throws Exception {
        GetRandomProblemResult result = new GetRandomProblemResult(
                1L,
                "문제 설명",
                List.of(
                        new GetRandomProblemChoiceResult(1, "보기 1"),
                        new GetRandomProblemChoiceResult(2, "보기 2")
                ),
                67
        );

        when(getRandomProblemService.getRandomProblem(any())).thenReturn(result);

        mockMvc.perform(get("/api/problems/random")
                        .queryParam("chapterId", "1")
                        .queryParam("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(1))
                .andExpect(jsonPath("$.data.content").value("문제 설명"))
                .andExpect(jsonPath("$.data.choices[0]").value("보기 1"))
                .andExpect(jsonPath("$.data.answerCorrectRate").value(67))
                .andExpect(jsonPath("$.error").isEmpty());

        verify(getRandomProblemService).getRandomProblem(any());
    }

    @Test
    @DisplayName("랜덤 문제 요청 검증 실패면 400을 반환한다")
    void returnBadRequestWhenRandomRequestInvalid() throws Exception {
        mockMvc.perform(get("/api/problems/random")
                .queryParam("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("랜덤 문제 요청에서 챕터가 없으면 404를 반환한다")
    void returnNotFoundWhenChapterMissing() throws Exception {
        when(getRandomProblemService.getRandomProblem(any()))
                .thenThrow(new ChapterNotFoundException(999L));

        mockMvc.perform(get("/api/problems/random")
                .queryParam("chapterId", "1")
                .queryParam("userId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAPTER_NOT_FOUND"));
    }

    @Test
    @DisplayName("출제 가능한 문제가 없으면 409를 반환한다")
    void returnConflictWhenNoAvailableProblemExists() throws Exception {
        when(getRandomProblemService.getRandomProblem(any()))
                .thenThrow(new NoAvailableProblemException(1L, 1L));

        mockMvc.perform(get("/api/problems/random")
                .queryParam("chapterId", "1")
                .queryParam("userId", "1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NO_AVAILABLE_PROBLEM"));
    }

    @Test
    @DisplayName("문제 넘기기 정상 응답을 반환한다")
    void returnNextProblemWhenSkipSucceeds() throws Exception {
        when(skipProblemService.skipProblem(any()))
                .thenReturn(new GetRandomProblemResult(
                        101L,
                        "다음 문제",
                        List.of(
                                new GetRandomProblemChoiceResult(1, "보기1"),
                                new GetRandomProblemChoiceResult(2, "보기2")
                        ),
                        null
                ));

        mockMvc.perform(post("/api/problems/skip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, 1L, 100L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(101))
                .andExpect(jsonPath("$.data.content").value("다음 문제"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("문제 넘기기에서 잘못된 요청이면 400을 반환한다")
    void returnBadRequestWhenSkipRequestInvalid() throws Exception {
        mockMvc.perform(post("/api/problems/skip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, null, 100L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("문제 넘기기에서 문제가 없으면 404를 반환한다")
    void returnNotFoundWhenProblemMissingInSkip() throws Exception {
        when(skipProblemService.skipProblem(any()))
                .thenThrow(new ProblemNotFoundInChapterException(1L, 100L));

        mockMvc.perform(post("/api/problems/skip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, 1L, 100L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROBLEM_NOT_FOUND_IN_CHAPTER"));
    }

    @Test
    @DisplayName("풀었던 문제 상세를 정상 반환한다")
    void returnSolvedProblemDetail() throws Exception {
        when(getSolvedProblemDetailService.getDetail(any()))
                .thenReturn(new GetSolvedProblemDetailResult(
                        3L,
                        ProblemAnswerFormat.OBJECTIVE,
                        AnswerStatus.PARTIAL,
                        "해설",
                        List.of("1", "2"),
                        List.of("1", "3"),
                        67
                ));

        mockMvc.perform(get("/api/problems/detail")
                .queryParam("userId", "1")
                .queryParam("problemId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(3))
                .andExpect(jsonPath("$.data.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.data.problemAnswers[0]").value("1"))
                .andExpect(jsonPath("$.data.userAnswers[1]").value("3"))
                .andExpect(jsonPath("$.data.answerCorrectRate").value(67))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("풀었던 문제 상세 요청이 잘못되면 400을 반환한다")
    void returnBadRequestWhenDetailRequestInvalid() throws Exception {
        mockMvc.perform(get("/api/problems/detail")
                .queryParam("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("풀었던 문제 상세 이력이 없으면 404를 반환한다")
    void returnNotFoundWhenSolvedProblemDetailMissing() throws Exception {
        when(getSolvedProblemDetailService.getDetail(any()))
                .thenThrow(new SolvedProblemNotFoundException(1L, 3L));

        mockMvc.perform(get("/api/problems/detail")
                .queryParam("userId", "1")
                .queryParam("problemId", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("SOLVED_PROBLEM_NOT_FOUND"));
    }

    @Test
    @DisplayName("문제 제출 정상 응답을 반환한다")
    void returnGradingResultWhenSubmitSucceeds() throws Exception {
        when(submitProblemAnswerService.submit(any()))
                .thenReturn(new SubmitProblemAnswerResult(1001L, ProblemAnswerFormat.OBJECTIVE, AnswerStatus.PARTIAL, "해설", List.of("1", "2")));

        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.problemId").value(1001))
                .andExpect(jsonPath("$.data.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.data.problemAnswers[0]").value("1"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("문제 제출에서 문제가 없으면 404를 반환한다")
    void returnNotFoundWhenProblemMissingInSubmit() throws Exception {
        when(submitProblemAnswerService.submit(any()))
                .thenThrow(new ProblemNotFoundInChapterException(null, 999L));

        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(999L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), null))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROBLEM_NOT_FOUND_IN_CHAPTER"));
    }

    @Test
    @DisplayName("문제 제출에서 답안 형식이 맞지 않으면 409를 반환한다")
    void returnConflictWhenAnswerTypeMismatch() throws Exception {
        when(submitProblemAnswerService.submit(any()))
                .thenThrow(new ProblemAnswerTypeMismatchException(1001L));

        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), null))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROBLEM_ANSWER_TYPE_MISMATCH"));
    }

    @Test
    @DisplayName("객관식 제출에서 선택지가 비어 있으면 400을 반환한다")
    void returnBadRequestWhenObjectiveChoicesMissing() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(), null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("selectedChoices: selectedChoices must not be empty for OBJECTIVE answerType"));
    }

    @Test
    @DisplayName("주관식 제출에서 답안이 비어 있으면 400을 반환한다")
    void returnBadRequestWhenSubjectiveAnswerMissing() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.SUBJECTIVE, null, "   "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("subjectiveAnswer: subjectiveAnswer must not be blank for SUBJECTIVE answerType"));
    }

    @Test
    @DisplayName("객관식 제출에서 주관식 답안이 같이 오면 400을 반환한다")
    void returnBadRequestWhenObjectiveContainsSubjectiveAnswer() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), "text"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("subjectiveAnswer: subjectiveAnswer must be blank for OBJECTIVE answerType"));
    }

    @Test
    @DisplayName("주관식 제출에서 객관식 선택지가 같이 오면 400을 반환한다")
    void returnBadRequestWhenSubjectiveContainsSelectedChoices() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.SUBJECTIVE, List.of(1), "answer"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("selectedChoices: selectedChoices must be empty for SUBJECTIVE answerType"));
    }

    @Test
    @DisplayName("객관식 제출에서 중복 선택지가 오면 400을 반환한다")
    void returnBadRequestWhenObjectiveContainsDuplicateChoices() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 1), null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("selectedChoices: selectedChoices must not contain duplicates"));
    }

    @Test
    @DisplayName("문제 제출에서 존재하지 않는 선택지 번호면 400을 반환한다")
    void returnBadRequestWhenProblemChoiceInvalid() throws Exception {
        when(submitProblemAnswerService.submit(any()))
                .thenThrow(new InvalidProblemChoiceException(1001L, List.of(7)));

        mockMvc.perform(post("/api/problems/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(7), null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PROBLEM_CHOICE"));
    }

    @Test
    @DisplayName("예상하지 못한 런타임 예외가 발생하면 500 공통 응답을 반환한다")
    void returnInternalServerErrorWhenUnexpectedRuntimeExceptionOccurs() throws Exception {
        when(getRandomProblemService.getRandomProblem(any()))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/problems/random")
                .queryParam("chapterId", "1")
                .queryParam("userId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Unexpected server error"));
}
}
