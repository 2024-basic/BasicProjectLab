package com.even.labserver.problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "Problem")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemService problemService;

    @GetMapping("/problem/{id}")
    @Operation(summary = "Get a problem by ID")
    public ResponseEntity<?> getProblem(@PathVariable(name = "id", required = true) Integer id) {
        if (1000 > id || id > 38000) return ResponseEntity.badRequest().body("Invalid problem ID");

        var problem = problemService.findProblemById(id);
        if (problem == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/problems")
    @Operation(summary = "Get a range of problems")
    public ResponseEntity<?> getProblems(@RequestParam(name = "start", required = true) Integer start,
                                         @RequestParam(name = "end", required = true) Integer end) {
        if (1000 > start || start > 38000 || 1000 > end || end > 38000 || start > end)
            return ResponseEntity.badRequest().body("Invalid range");

        var problems = problemService.findProblemsRange(start, end);
        return ResponseEntity.ok(problems);
    }
}
