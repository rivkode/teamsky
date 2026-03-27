package com.project.quiz.api.solving;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "문제 제출 요청")
@ValidSubmitProblemAnswerRequest
public record SubmitProblemAnswerRequest(
        @Schema(description = "문제 ID", example = "1001")
        @NotNull(message = "problemId is required")
        @Positive(message = "problemId must be positive")
        Long problemId,

        @Schema(description = "사용자 ID", example = "1")
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be positive")
        Long userId,

        @Schema(description = "답안 형식", example = "OBJECTIVE")
        @NotNull(message = "answerType is required")
        ProblemAnswerFormat answerType,

        @ArraySchema(schema = @Schema(description = "객관식 선택지 번호 목록", example = "1"))
        List<Integer> selectedChoices,

        @Schema(description = "주관식 답안", example = "싱글톤")
        String subjectiveAnswer
) {
}
