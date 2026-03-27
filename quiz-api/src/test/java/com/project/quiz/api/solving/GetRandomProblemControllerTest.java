package com.project.quiz.api.solving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.port.in.GetRandomProblemChoiceResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemUseCase;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetRandomProblemController.class)
@ContextConfiguration(classes = {
        com.project.quiz.api.TestApiApplication.class,
        GetRandomProblemController.class,
        GlobalExceptionHandler.class
})
class GetRandomProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetRandomProblemUseCase getRandomProblemUseCase;

    @Test
    @DisplayName("정상 요청이면 랜덤 문제를 반환한다")
    void returnRandomProblem() throws Exception {
        GetRandomProblemRequest request = new GetRandomProblemRequest(1L, 1L);
        GetRandomProblemResult result = new GetRandomProblemResult(
                100L,
                "문제 설명",
                List.of(
                        new GetRandomProblemChoiceResult(1, "지문1"),
                        new GetRandomProblemChoiceResult(2, "지문2"),
                        new GetRandomProblemChoiceResult(3, "지문3"),
                        new GetRandomProblemChoiceResult(4, "지문4"),
                        new GetRandomProblemChoiceResult(5, "지문5")
                ),
                67
        );

        when(getRandomProblemUseCase.getRandomProblem(any())).thenReturn(result);

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(100))
                .andExpect(jsonPath("$.content").value("문제 설명"))
                .andExpect(jsonPath("$.choices[0]").value("지문1"))
                .andExpect(jsonPath("$.choices[4]").value("지문5"))
                .andExpect(jsonPath("$.answerCorrectRate").value(67));

        verify(getRandomProblemUseCase).getRandomProblem(any());
    }

    @Test
    @DisplayName("validation 실패면 400을 반환한다")
    void returnBadRequestWhenValidationFails() throws Exception {
        GetRandomProblemRequest request = new GetRandomProblemRequest(0L, null);

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    @DisplayName("존재하지 않는 챕터면 404를 반환한다")
    void returnNotFoundWhenChapterDoesNotExist() throws Exception {
        GetRandomProblemRequest request = new GetRandomProblemRequest(1L, 999L);

        when(getRandomProblemUseCase.getRandomProblem(any()))
                .thenThrow(new ChapterNotFoundException(999L));

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHAPTER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Chapter not found. chapterId=999"));
    }

    @Test
    @DisplayName("출제 가능한 문제가 없으면 409를 반환한다")
    void returnConflictWhenNoAvailableProblemExists() throws Exception {
        GetRandomProblemRequest request = new GetRandomProblemRequest(1L, 1L);

        when(getRandomProblemUseCase.getRandomProblem(any()))
                .thenThrow(new NoAvailableProblemException(1L, 1L));

        mockMvc.perform(post("/api/problems/random")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("NO_AVAILABLE_PROBLEM"))
                .andExpect(jsonPath("$.message").value("No available problem for userId=1, chapterId=1"));
    }
}
