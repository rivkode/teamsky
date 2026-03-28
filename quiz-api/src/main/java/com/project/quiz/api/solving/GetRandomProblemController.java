package com.project.quiz.api.problem;

import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.service.GetRandomProblemService;
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
public class GetRandomProblemController {

    private final GetRandomProblemService getRandomProblemService;

    @Operation(
            summary = "랜덤 미풀이 문제 조회",
            description = "선택한 단원에서 이미 푼 문제와 직전에 건너뛴 문제를 제외하고 랜덤 문제 1개를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "랜덤 문제가 정상적으로 반환되었습니다."),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 단원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "해당 단원에 더 이상 출제 가능한 문제가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/random")
    public ResponseEntity<GetRandomProblemResponse> getRandomProblem(
            @Valid @RequestBody GetRandomProblemRequest request
    ) {
        GetRandomProblemResult result = getRandomProblemService.getRandomProblem(
                new GetRandomProblemCommand(request.userId(), request.chapterId())
        );

        return ResponseEntity.ok(new GetRandomProblemResponse(
                result.problemId(),
                result.content(),
                result.choices().stream()
                        .map(choice -> choice.content())
                        .toList(),
                result.answerCorrectRate()
        ));
    }
}
