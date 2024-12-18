package com.even.labserver.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Tag(name = "Community", description = "커뮤니티 관련 API")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

//    @GetMapping("/post/list")
//    @Operation(summary = "게시글 목록 조회")
//    public ResponseEntity<?> getPosts(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
//                                      @RequestParam(value = "sort", defaultValue = "createdDate", required = false) String sort,
//                                      @RequestParam(value = "isAsc", defaultValue = "false", required = false) boolean isAsc) {
//        return ResponseEntity.ok(communityService.getPosts(page, sort, isAsc));
//    }
//
//    @GetMapping("/post/{id}")
//    @Operation(summary = "게시글 조회")
//    public ResponseEntity<?> getPost(@PathVariable(name = "id") int id) {
//        return ResponseEntity.ok(communityService.getPost(id));
//    }
//
//    @PostMapping("/post")
//    @Operation(summary = "게시글 추가", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
//            @ExampleObject(value = "{\"title\": \"제목\", \"content\": \"내용\", \"author\": \"작성자\"}")
//    })))
//    public ResponseEntity<?> addPost(@RequestBody PostDto postDto) {
//        return ResponseEntity.ok(communityService.addPost(postDto));
//    }
//
//    @DeleteMapping("/post/{id}")
//    @Operation(summary = "게시글 삭제")
//    public ResponseEntity<?> removePost(@PathVariable(name = "id") int id) {
//        return ResponseEntity.ok(communityService.removePost(id));
//    }
//
//    @PutMapping("/post/{id}")
//    @Operation(summary = "게시글 수정", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
//            @ExampleObject(value = "{\"title\": \"제목\", \"content\": \"내용\"")
//    })))
//    public ResponseEntity<?> updatePost(@PathVariable(name = "id") int id, @RequestBody PostDto postDto) {
//        postDto.setPostId(id);
//        return ResponseEntity.ok(communityService.updatePost(postDto));
//    }
//
//    @PostMapping("/post/{id}/like")
//    @Operation(summary = "게시글 좋아요")
//    public ResponseEntity<?> likePost(@PathVariable(name = "id") int id) {
//        return ResponseEntity.ok(communityService.likePost(id));
//    }
//
//    @PostMapping("/post/{id}/comment")
//    @Operation(summary = "댓글 추가", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
//            @ExampleObject(value = "{\"content\": \"내용\", \"author\": \"작성자\"}")
//    })))
//    public ResponseEntity<?> addComment(@PathVariable(name = "id") int postId, @RequestBody CommentDto commentDto) {
//        commentDto.setPostId(postId);
//        return ResponseEntity.ok(communityService.addComment(commentDto));
//    }
//
//    @DeleteMapping("/post/{id}/comment/{commentId}")
//    @Operation(summary = "댓글 삭제")
//    public ResponseEntity<?> removeComment(@PathVariable("id") int postId, @PathVariable("commentId") int commentId) {
//
//        return ResponseEntity.ok(communityService.removeComment(postId, commentId));
//    }
//
//    @PutMapping("/post/{id}/comment/{commentId}")
//    @Operation(summary = "댓글 수정", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
//            @ExampleObject(value = "{\"content\": \"내용\"}")
//    })))
//    public ResponseEntity<?> updateComment(@PathVariable("id") int postId, @PathVariable("commentId") int commentId, @RequestBody CommentDto commentDto) {
//        commentDto.setPostId(postId);
//        commentDto.setCommentId(commentId);
//        return ResponseEntity.ok(communityService.updateComment(commentDto));
//    }
}
