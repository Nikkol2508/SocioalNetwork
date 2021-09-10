package application.controllers;

import application.models.dto.CommentDto;
import application.models.dto.MessageResponseDto;
import application.models.dto.PostDto;
import application.models.requests.CommentRequest;
import application.models.requests.PostRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @GetMapping
    public ResponseEntity<GeneralListResponse<PostDto>> searchPosts(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "date_from", required = false) Long dateFrom,
            @RequestParam(value = "date_to", required = false) Long dateTo,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(postsService
                .getPosts(text, author, dateFrom, dateTo, tags), offset, itemPerPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<PostDto>> getPost(@PathVariable int id) {

        log.info("Get post {}");
        return ResponseEntity.ok(new GeneralResponse<>(postsService.getPostResponse(id)));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<GeneralListResponse<CommentDto>> getComments(
            @PathVariable String id,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(postsService.getCommentsResponse(id), offset, itemPerPage));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<GeneralResponse<CommentDto>> postComment(@PathVariable String id,
                                                                   @RequestBody CommentRequest commentRequest) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.setComment(id, commentRequest)));
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<GeneralResponse<CommentDto>> editComment(@RequestBody CommentRequest request,
                                                                   @PathVariable String id,
                                                                   @PathVariable int comment_id) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.editComment(request, id, comment_id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<PostDto>> editPost(@RequestBody PostRequest postRequest,
                                                             @PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.editPost(postRequest, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> deletePost(@PathVariable int id) {

        log.info("Delete post {}", id);
        return ResponseEntity.ok(new GeneralResponse<>(postsService.deletePost(id)));
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<GeneralResponse<HashMap<String, Integer>>> deleteComment(@PathVariable String id,
                                                                                   @PathVariable int comment_id) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.deleteComment(id, comment_id)));
    }
}
