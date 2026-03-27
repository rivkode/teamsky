package com.project.quiz.api.solving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.port.in.GetRandomProblemResult;
import com.project.quiz.application.solving.port.in.SkipProblemUseCase;
import com.project.quiz.api.TestApiApplication;
import com.project.quiz.api.common.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkipProblemController.class)
@ContextConfiguration(classes = {
        TestApiApplication.class,
        SkipProblemController.class,
        GlobalExceptionHandler.class
})
class SkipProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkipProblemUseCase skipProblemUseCase;

    @Test
    @DisplayName("정상 요청이면 건너뛴 뒤 다음 문제를 반환한다")
    void returnNextRandomProblem() throws Exception {
        when(skipProblemUseCase.skipProblem(any()))
                .thenReturn(new GetRandomProblemResult(1002L, "다음 문제", List.of(), null));

        mockMvc.perform(post("/api/problems/skip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, 1L, 1001L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(1002))
                .andExpect(jsonPath("$.content").value("다음 문제"));
    }

    @Test
    @DisplayName("요청 검증 실패면 400을 반환한다")
    void returnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/problems/skip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, null, 0L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("문제가 단원에 없으면 404를 반환한다")
    void returnNotFoundWhenProblemNotInChapter() throws Exception {
        when(skipProblemUseCase.skipProblem(any()))
                .thenThrow(new ProblemNotFoundInChapterException(1L, 1001L));

        mockMvc.perform(post("/api/problems/skip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, 1L, 1001L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROBLEM_NOT_FOUND_IN_CHAPTER"));
    }

    @Test
    @DisplayName("출제 가능한 문제가 없으면 409를 반환한다")
    void returnConflictWhenNoAvailableProblemExists() throws Exception {
        when(skipProblemUseCase.skipProblem(any()))
                .thenThrow(new NoAvailableProblemException(1L, 1L));

        mockMvc.perform(post("/api/problems/skip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkipProblemRequest(1L, 1L, 1001L))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("NO_AVAILABLE_PROBLEM"));
    }
}
