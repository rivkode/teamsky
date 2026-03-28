package com.project.quiz.api.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "풀었던 문제 상세 조회 요청")
public record GetSolvedProblemDetailRequest(
        @Schema(description = "사용자 ID", example = "1")
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be positive")
        Long userId,

        @Schema(description = "문제 ID", example = "1003")
        @NotNull(message = "problemId is required")
        @Positive(message = "problemId must be positive")
        Long problemId
) {
}
