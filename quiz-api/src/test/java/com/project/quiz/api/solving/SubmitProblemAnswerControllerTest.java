package com.project.quiz.api.solving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerUseCase;
import com.project.quiz.api.TestApiApplication;
import com.project.quiz.api.common.GlobalExceptionHandler;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubmitProblemAnswerController.class)
@ContextConfiguration(classes = {
        TestApiApplication.class,
        SubmitProblemAnswerController.class,
        GlobalExceptionHandler.class
})
class SubmitProblemAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubmitProblemAnswerUseCase submitProblemAnswerUseCase;

    @Test
    @DisplayName("정상 제출이면 채점 결과를 반환한다")
    void returnGradingResult() throws Exception {
        when(submitProblemAnswerUseCase.submit(any()))
                .thenReturn(new SubmitProblemAnswerResult(1001L, ProblemAnswerFormat.OBJECTIVE, AnswerStatus.PARTIAL, "해설", List.of("1", "2")));

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(1001))
                .andExpect(jsonPath("$.answerStatus").value("PARTIAL"))
                .andExpect(jsonPath("$.problemAnswers[0]").value("1"));
    }

    @Test
    @DisplayName("문제를 찾을 수 없으면 404를 반환한다")
    void returnNotFoundWhenProblemMissing() throws Exception {
        when(submitProblemAnswerUseCase.submit(any()))
                .thenThrow(new ProblemNotFoundInChapterException(null, 999L));

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(999L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), null))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROBLEM_NOT_FOUND_IN_CHAPTER"));
    }

    @Test
    @DisplayName("답안 형식이 맞지 않으면 409를 반환한다")
    void returnConflictWhenAnswerTypeMismatch() throws Exception {
        when(submitProblemAnswerUseCase.submit(any()))
                .thenThrow(new ProblemAnswerTypeMismatchException(1001L));

        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), null))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PROBLEM_ANSWER_TYPE_MISMATCH"));
    }

    @Test
    @DisplayName("객관식 제출에서 선택지가 비어 있으면 400을 반환한다")
    void returnBadRequestWhenObjectiveChoicesMissing() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(), null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("selectedChoices: selectedChoices must not be empty for OBJECTIVE answerType"));
    }

    @Test
    @DisplayName("주관식 제출에서 답안이 비어 있으면 400을 반환한다")
    void returnBadRequestWhenSubjectiveAnswerMissing() throws Exception {
        mockMvc.perform(post("/api/problems/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.SUBJECTIVE, null, "   "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("subjectiveAnswer: subjectiveAnswer must not be blank for SUBJECTIVE answerType"));
    }
}
