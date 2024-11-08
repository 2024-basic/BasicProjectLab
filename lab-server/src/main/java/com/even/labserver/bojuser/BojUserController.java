package com.even.labserver.bojuser;

import com.even.labserver.utils.ScrapeManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Tag(name = "BojUser")
@RequiredArgsConstructor
public class BojUserController {
    private final BojUserService bojUserService;

    @GetMapping("/user/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<?> getUser(@PathVariable(name = "id", required = true) String id) {
        return ResponseEntity.ok(bojUserService.findUserById(id));
    }
}
