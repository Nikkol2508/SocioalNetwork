package application.controllers;

import application.models.Comment;
import application.models.Post;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @GetMapping
    public ResponseEntity<GeneralListResponse<Post>> searchPosts(@RequestParam(value = "text") String text,
                                                                @RequestParam(value = "date_from", required = false) Long dateFrom,
                                                                @RequestParam(value = "date_to", required = false) Long dateTo) {
        return ResponseEntity.ok(postsService.getPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<Post>> getPost(@PathVariable int id) {
        return ResponseEntity.ok(postsService.getPost(id));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<GeneralListResponse<Comment>> getComments(@PathVariable int id) {
        return ResponseEntity.ok(postsService.getComments(id));
    }
}
