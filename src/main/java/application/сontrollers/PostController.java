package application.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {

    //post
    @GetMapping("api/v1/post")
    public ResponseEntity findPost(@PathVariable String query, int dateFrom, int dateTo, int offset, int itemPerPage) {
        return null;
    }

    @GetMapping("api/v1/post/{id}")
    public ResponseEntity findPostId(@PathVariable int id) {
        return null;
    }

    @PutMapping("api/v1/post/{id}")
    public ResponseEntity changePost(@PathVariable int id, int publishDate) {
        return null;
    }

    @PutMapping("api/v1/post/{id}/recover")
    public ResponseEntity recoverPost(@PathVariable int id) {
        return null;
    }

    @DeleteMapping("api/v1/post/{id}")
    public ResponseEntity deletePost(@PathVariable int id) {
        return null;
    }

    //comments
    @GetMapping("api/v1/post/{id}/comments")
    public ResponseEntity getComments(@PathVariable int id, int offset, int itemPerPage) {
        return null;
    }

    @PostMapping("api/v1/post/{id}/comments")
    public int postComment(int id) {
        return 1;
    }

    @PutMapping("api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity changeComment(@PathVariable int postId,@PathVariable int commentId) {
        return null;
    }

    @PutMapping("api/v1/post/{id}/comments/{comment_id}/recover")
    public ResponseEntity recoverComment(@PathVariable int postId,@PathVariable int commentId) {
        return null;
    }

    @DeleteMapping("api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity deleteComment(@PathVariable int postId,@PathVariable int commentId) {
        return null;
    }

    //reports
    @PostMapping("api/v1/post/{id}/report")
    public int postReport(@PathVariable int id) {
        return 1;
    }

    @PutMapping("api/v1/post/{id}/comments/{comment_id}/report")
    public int reportComment(@PathVariable int postId,@PathVariable int commentId) {
        return 1;
    }
}
