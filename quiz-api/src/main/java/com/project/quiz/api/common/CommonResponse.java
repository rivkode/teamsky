package com.project.quiz.api.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 공통 응답")
public record CommonResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "성공 응답 데이터", nullable = true)
        T data,

        @Schema(description = "오류 정보", nullable = true)
        ErrorResponse error
) {
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }

    public static <T> CommonResponse<T> failure(String code, String message) {
        return new CommonResponse<>(false, null, new ErrorResponse(code, message));
    }
}
