package com.project.quiz.api.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 오류 응답")
public record ErrorResponse(
        @Schema(description = "애플리케이션 오류 코드", example = "CHAPTER_NOT_FOUND")
        String code,
        @Schema(description = "오류 메시지", example = "Chapter not found. chapterId=1")
        String message
) {
}
