package com.even.labserver.problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    @GetMapping("/recommended-problems")
    @Operation(summary = "추천 문제들을 반환합니다", responses = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(examples = {
                            @ExampleObject(summary = "page=1, kw='스타', levelStart=1, levelEnd=30, isAsc=false, userId='tjgus1668', searchMode=false 일 때의 결과",
                                    value = "{\n" +
                                    "  \"content\": [\n" +
                                    "    {\n" +
                                    "      \"problemId\": 2712,\n" +
                                    "      \"title\": \"미국 스타일\",\n" +
                                    "      \"level\": 3,\n" +
                                    "      \"solvedCount\": 2099,\n" +
                                    "      \"votedCount\": 63,\n" +
                                    "      \"givesNoRating\": false,\n" +
                                    "      \"averageTries\": 1.516,\n" +
                                    "      \"tags\": [\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"사칙연산\",\n" +
                                    "          \"problemCount\": 1139,\n" +
                                    "          \"key\": \"arithmetic\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"구현\",\n" +
                                    "          \"problemCount\": 5762,\n" +
                                    "          \"key\": \"implementation\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"수학\",\n" +
                                    "          \"problemCount\": 6640,\n" +
                                    "          \"key\": \"math\"\n" +
                                    "        }\n" +
                                    "      ],\n" +
                                    "      \"source\": null\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"problemId\": 3967,\n" +
                                    "      \"title\": \"매직 스타\",\n" +
                                    "      \"level\": 11,\n" +
                                    "      \"solvedCount\": 1083,\n" +
                                    "      \"votedCount\": 25,\n" +
                                    "      \"givesNoRating\": false,\n" +
                                    "      \"averageTries\": 1.8975,\n" +
                                    "      \"tags\": [\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"백트래킹\",\n" +
                                    "          \"problemCount\": 551,\n" +
                                    "          \"key\": \"backtracking\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"브루트포스 알고리즘\",\n" +
                                    "          \"problemCount\": 2299,\n" +
                                    "          \"key\": \"bruteforcing\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"구현\",\n" +
                                    "          \"problemCount\": 5762,\n" +
                                    "          \"key\": \"implementation\"\n" +
                                    "        }\n" +
                                    "      ],\n" +
                                    "      \"source\": null\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"problemId\": 25214,\n" +
                                    "      \"title\": \"크림 파스타\",\n" +
                                    "      \"level\": 7,\n" +
                                    "      \"solvedCount\": 837,\n" +
                                    "      \"votedCount\": 54,\n" +
                                    "      \"givesNoRating\": false,\n" +
                                    "      \"averageTries\": 1.7694,\n" +
                                    "      \"tags\": [\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"다이나믹 프로그래밍\",\n" +
                                    "          \"problemCount\": 4234,\n" +
                                    "          \"key\": \"dp\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"displayName\": \"그리디 알고리즘\",\n" +
                                    "          \"problemCount\": 2725,\n" +
                                    "          \"key\": \"greedy\"\n" +
                                    "        }\n" +
                                    "      ],\n" +
                                    "      \"source\": null\n" +
                                    "    }\n" +
                                    "  ],\n" +
                                    "  \"page\": {\n" +
                                    "    \"size\": 3,\n" +
                                    "    \"number\": 1,\n" +
                                    "    \"totalElements\": 18,\n" +
                                    "    \"totalPages\": 6\n" +
                                    "  }\n" +
                                    "}")
                    })
            })
    })
//    @Cacheable(value = "recommendedProblems", key = "{#page, #kw, #levelStart, #levelEnd, #isAsc, #userId, #searchMode}")
    public ResponseEntity<?> getRecommendedProblems(@Parameter(description = "페이지 번호, 0부터 시작합니다")
                                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,

                                                    @Parameter(description = "검색 키워드")
                                                    @RequestParam(name = "kw", required = false, defaultValue = "") String kw,

                                                    @Parameter(description = "문제 난이도의 시작 범위")
                                                    @RequestParam(name = "levelStart", required = false, defaultValue = "1") Integer levelStart,

                                                    @Parameter(description = "문제 난이도의 끝 범위")
                                                    @RequestParam(name = "levelEnd", required = false, defaultValue = "30") Integer levelEnd,

                                                    @Parameter(description = "오름차순 정렬 여부")
                                                    @RequestParam(name = "isAsc", required = false, defaultValue = "false") Boolean isAsc,

                                                    @Parameter(description = "사용자 ID")
                                                    @RequestParam(name = "userId", required = false, defaultValue = "") String userId,

                                                    @Parameter(description = "검색 모드 여부, 검색 모드일 경우, page, kw, isAsc만 사용되고 푼 문제 수로 정렬됩니다.")
                                                    @RequestParam(name = "searchMode", required = true, defaultValue = "false") Boolean searchMode) {
        if (page < 0 || levelStart < 1 || levelEnd > 30 || levelStart > levelEnd)
            return ResponseEntity.badRequest().body("Invalid parameters");

        var problems = problemService.getRecommendedProblems(page, kw, levelStart, levelEnd, isAsc, userId, searchMode);
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
