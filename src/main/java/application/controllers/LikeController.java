package application.controllers;

import application.models.LikeResponseDto;
import application.models.responses.GeneralResponse;
import application.models.requests.LikeRequest;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

    private final PostsService postsService;

    @GetMapping("/liked")
    public ResponseEntity<GeneralResponse<Map<String, Boolean>>> getLiked(@RequestParam(value = "user_id", required = false) int user_id,
                                                                          @RequestParam(value = "item_id") int itemId,
                                                                          @RequestParam(value = "type") String type) {
        return ResponseEntity.ok(postsService.getLiked(user_id, itemId, type));
    }

    @GetMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> getLikes(@RequestParam(value = "item_id") int itemId,
                                                                     @RequestParam(value = "type") String type) {
        return ResponseEntity.ok(postsService.getLikes(itemId, type));
    }

    @PutMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> setLike(@RequestBody LikeRequest request) {
        return ResponseEntity.ok(postsService.setLikes(request));
    }

    @DeleteMapping("/likes")
    public ResponseEntity<GeneralResponse<Map<String, String>>> deleteLike(@RequestParam(value = "item_id") int itemId,
                                                               @RequestParam(value = "type") String type) {
        return ResponseEntity.ok(postsService.deleteLike(itemId, type));
    }
}
