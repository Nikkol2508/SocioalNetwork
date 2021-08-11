package application.controllers;

import application.models.LikeResponseDto;
import application.models.requests.LikeRequest;
import application.models.responses.GeneralResponse;
import application.service.PostsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

  private final PostsService postsService;

  @GetMapping("/liked")
  public ResponseEntity<GeneralResponse<Map<String, Boolean>>> getLiked(
      @RequestParam(value = "user_id", required = false) int user_id,
      @RequestParam(value = "item_id") int itemId,
      @RequestParam(value = "type") String type) {
    return ResponseEntity.ok(postsService.getLiked(user_id, itemId, type));
  }

  @GetMapping("/likes")
  public ResponseEntity<GeneralResponse<LikeResponseDto>> getLikes(
      @RequestParam(value = "item_id") int itemId,
      @RequestParam(value = "type") String type) {
    return ResponseEntity.ok(postsService.getLikes(itemId, type));
  }

  @PutMapping("/likes")
  public ResponseEntity<GeneralResponse<LikeResponseDto>> setLike(
      @RequestBody LikeRequest request) {
    return ResponseEntity.ok(postsService.setLikes(request));
  }


}
