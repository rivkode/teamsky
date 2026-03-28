package com.project.quiz.api.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "랜덤 문제 조회 요청")
public record GetRandomProblemRequest(
        @Schema(description = "단원 ID", example = "1")
        @NotNull(message = "chapterId is required")
        @Positive(message = "chapterId must be positive")
        Long chapterId,

        @Schema(description = "사용자 ID", example = "1")
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be positive")
        Long userId
) {
}
