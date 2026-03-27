package com.project.quiz.api.solving;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "문제 넘기기 요청")
public record SkipProblemRequest(
        @Schema(description = "단원 ID", example = "1")
        @NotNull(message = "chapterId is required")
        @Positive(message = "chapterId must be positive")
        Long chapterId,

        @Schema(description = "사용자 ID", example = "1")
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be positive")
        Long userId,

        @Schema(description = "건너뛸 현재 문제 ID", example = "1001")
        @NotNull(message = "problemId is required")
        @Positive(message = "problemId must be positive")
        Long problemId
) {
}
