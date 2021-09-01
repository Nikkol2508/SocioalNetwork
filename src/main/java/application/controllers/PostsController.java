package application.controllers;

import application.models.dto.CommentDto;
import application.models.dto.MessageResponseDto;
import application.models.dto.PostDto;
import application.models.requests.CommentRequest;
import application.models.requests.PostRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.FeedsService;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final FeedsService feedsService;

    @GetMapping
    public ResponseEntity<GeneralListResponse<PostDto>> searchPosts(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "date_from", required = false) Long dateFrom,
            @RequestParam(value = "date_to", required = false) Long dateTo) {
        return ResponseEntity.ok(postsService.getPosts(text, author, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<PostDto>> getPost(@PathVariable int id) {
        return ResponseEntity.ok(postsService.getPostResponse(id));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<GeneralListResponse<CommentDto>> getComments(@PathVariable String id) {
        return ResponseEntity.ok(postsService.getCommentsResponse(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<GeneralResponse<CommentDto>> postComment(@PathVariable String id,
                                                                @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(postsService.setComment(id, commentRequest));
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<GeneralResponse<CommentDto>> editComment(@RequestBody CommentRequest request,
                                                                @PathVariable String id,
                                                                @PathVariable int comment_id) {
        return ResponseEntity.ok(postsService.editComment(request, id, comment_id));
    }

    @PutMapping("/{id}")
    public ResponseEntity <GeneralResponse<PostDto>> editPost(@RequestBody PostRequest postRequest,
                                                              @PathVariable int id) {
        return ResponseEntity.ok(postsService.editPost(postRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <GeneralResponse<MessageResponseDto>> deletePost (@PathVariable int id) {
        return ResponseEntity.ok(postsService.deletePost(id));
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<GeneralResponse<HashMap<String, Integer>>> deleteComment(@PathVariable String id,
                                                                                   @PathVariable int comment_id) {
        return ResponseEntity.ok(postsService.deleteComment(id, comment_id));
    }
}
