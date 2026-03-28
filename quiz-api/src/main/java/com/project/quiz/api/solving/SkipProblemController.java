package com.project.quiz.api.problem;

import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.model.SkipProblemCommand;
import com.project.quiz.application.solving.service.SkipProblemService;
import com.project.quiz.api.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "단원별 문제 풀이", description = "단원별 랜덤 문제 조회와 풀이 관련 엔드포인트")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class SkipProblemController {

    private final SkipProblemService skipProblemService;

    @Operation(
            summary = "문제 넘기기",
            description = "현재 문제를 건너뛴 뒤 같은 단원에서 다음 랜덤 문제 1개를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문제를 건너뛰고 다음 문제가 정상적으로 반환되었습니다."),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 단원 또는 문제가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "건너뛴 이후 더 이상 출제 가능한 문제가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/skip")
    public ResponseEntity<GetRandomProblemResponse> skipProblem(@Valid @RequestBody SkipProblemRequest request) {
        GetRandomProblemResult result = skipProblemService.skipProblem(
                new SkipProblemCommand(request.userId(), request.chapterId(), request.problemId())
        );

        return ResponseEntity.ok(new GetRandomProblemResponse(
                result.problemId(),
                result.content(),
                result.choices().stream().map(choice -> choice.content()).toList(),
                result.answerCorrectRate()
        ));
    }
}
