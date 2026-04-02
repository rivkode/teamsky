package com.project.quiz.api.problem;

import com.project.quiz.api.common.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SubmitProblemAnswerCommonResponse", description = "문제 제출 공통 응답")
public record SubmitProblemAnswerCommonResponse(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "성공 응답 데이터", nullable = true)
        SubmitProblemAnswerResponse data,

        @Schema(description = "오류 정보", nullable = true)
        ErrorResponse error
) {
}
