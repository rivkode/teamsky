package com.project.quiz.api.problem;

import com.project.quiz.api.common.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GetRandomProblemCommonResponse", description = "랜덤 문제 조회 공통 응답")
public record GetRandomProblemCommonResponse(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "성공 응답 데이터", nullable = true)
        GetRandomProblemResponse data,

        @Schema(description = "오류 정보", nullable = true)
        ErrorResponse error
) {
}
