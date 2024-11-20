package com.even.labserver.problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "Problem")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemService problemService;

    @GetMapping("/problem/{id}")
    @Operation(summary = "해당 ID에 대응되는 문제를 반환합니다", responses = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(examples = {
                            @ExampleObject(value = "{\n" +
                                    "  \"problemId\": 1000,\n" +
                                    "  \"title\": \"A+B\",\n" +
                                    "  \"level\": 1,\n" +
                                    "  \"solvedCount\": 316363,\n" +
                                    "  \"votedCount\": 371,\n" +
                                    "  \"givesNoRating\": false,\n" +
                                    "  \"averageTries\": 2.5822,\n" +
                                    "  \"tags\": [\n" +
                                    "    {\n" +
                                    "      \"displayName\": \"구현\",\n" +
                                    "      \"problemCount\": 5762,\n" +
                                    "      \"key\": \"implementation\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"displayName\": \"사칙연산\",\n" +
                                    "      \"problemCount\": 1139,\n" +
                                    "      \"key\": \"arithmetic\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"displayName\": \"수학\",\n" +
                                    "      \"problemCount\": 6640,\n" +
                                    "      \"key\": \"math\"\n" +
                                    "    }\n" +
                                    "  ],\n" +
                                    "  \"source\": null\n" +
                                    "}")
                    })
            })
    })
    public ResponseEntity<?> getProblem(@Parameter(description = "문제의 ID 번호", example = "1000")
                                        @PathVariable(name = "id", required = true) Integer id) {
        if (1000 > id || id > 38000) return ResponseEntity.badRequest().body("Invalid problem ID");

        var problem = problemService.findProblemById(id);
        if (problem == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/problems")
    @Operation(summary = "범위 내의 문제들을 반환합니다")
    public ResponseEntity<?> getProblems(@Parameter(description = "시작 문제 번호")
                                         @RequestParam(name = "start", required = true) Integer start,
                                         @Parameter(description = "끝 문제 번호")
                                         @RequestParam(name = "end", required = true) Integer end) {
        if (1000 > start || start > 38000 || 1000 > end || end > 38000 || start > end)
            return ResponseEntity.badRequest().body("Invalid range");

        var problems = problemService.findProblemsRange(start, end);
        return ResponseEntity.ok(problems);
    }

    @PostMapping("/scrape/problems/{size}")
    @Operation(summary = "스크래핑하지 않은 문제를 solved.ac에서 스크래핑합니다")
    public ResponseEntity<?> getProblems(@Parameter(description = "스크래핑할 문제의 목표 개수")
                                         @PathVariable(name = "size", required = true) Integer size,
                                         @Parameter(description = "스크래핑된 문제의 상세 정보를 반환할지 여부")
                                         @RequestParam(name = "detail", required = false, defaultValue = "false") Boolean detail) {
        if (size < 1) return ResponseEntity.badRequest().body("Invalid size");

        var problems = problemService.scrapeUnscrapedProblems(size);
        return ResponseEntity.ok(detail ? problems : problems.size());
    }
}
