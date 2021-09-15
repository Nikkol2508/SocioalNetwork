package application.controllers;

import application.models.dto.LikeResponseDto;
import application.models.requests.LikeRequest;
import application.models.responses.GeneralResponse;
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
    public ResponseEntity<GeneralResponse<Map<String, Boolean>>> getLiked(
            @RequestParam(value = "user_id", required = false) int userId,
            @RequestParam(value = "item_id") int itemId,
            @RequestParam(value = "type") String type) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.getLiked(userId, itemId, type)));
    }

    @GetMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> getLikes(@RequestParam(value = "item_id") int itemId,
                                                                     @RequestParam(value = "type") String type) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.getLikes(itemId, type)));
    }

    @PutMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> setLike(@RequestBody LikeRequest request) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.setLikes(request)));
    }

    @DeleteMapping("/likes")
    public ResponseEntity<GeneralResponse<Map<String, String>>> deleteLike(@RequestParam(value = "item_id") int itemId,
                                                                           @RequestParam(value = "type") String type) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.deleteLike(itemId, type)));
    }
}
