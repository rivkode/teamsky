package com.project.quiz.api.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CommonErrorResponse", description = "오류 공통 응답")
public record CommonErrorResponse(
        @Schema(description = "요청 성공 여부", example = "false")
        boolean success,

        @Schema(description = "성공 응답 데이터", nullable = true, example = "null")
        Object data,

        @Schema(description = "오류 정보")
        ErrorResponse error
) {
}
