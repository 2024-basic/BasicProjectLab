package com.even.labserver.bojuser;

import com.even.labserver.utils.ScrapeManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Tag(name = "BojUser")
@RequiredArgsConstructor
public class BojUserController {
    private final BojUserService bojUserService;

    @GetMapping("/user/{id}")
    @Operation(summary = "ID로 유저를 찾아 반환합니다.")
    public ResponseEntity<?> getUser(@PathVariable(name = "id", required = true) String id) {
        return ResponseEntity.ok(bojUserService.findUserById(id));
    }

    @GetMapping("/users")
    @Operation(summary = "DB에 저장된 모든 유저를 반환합니다.")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(bojUserService.findAllUsers());
    }

    @GetMapping("/ranking")
    @Operation(summary = "충남대학교의 랭킹 순위를 반환합니다.")
    @Cacheable(value = "ranking")
    public ResponseEntity<?> getRanking() {
        return ResponseEntity.ok(bojUserService.getRanking());
    }

    @PostMapping("/users")
    @Operation(summary = "모든 충남대 학생들을 DB에 추가합니다.")
    public ResponseEntity<?> addAllCnuUsers() {
        return ResponseEntity.ok(bojUserService.addAllUsers());
    }
}
