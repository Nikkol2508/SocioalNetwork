package application.controllers;

import application.models.dto.LikeResponseDto;
import application.models.requests.LikeRequest;
import application.models.responses.GeneralResponse;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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

        log.info("getLiked(): start():");
        log.debug("getLiked(): userId = {}, itemId = {}, type = {}", userId, itemId, type);
        GeneralResponse<Map<String, Boolean>> generalResponse = new GeneralResponse<>(postsService.getLiked(userId, itemId, type));
        log.debug("getLiked(): response = {}", generalResponse);
        log.info("getLiked(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> getLikes(@RequestParam(value = "item_id") int itemId,
                                                                     @RequestParam(value = "type") String type) {
        log.info("getLikes(): start():");
        log.debug("getLikes(): itemId = {}, type = {}", itemId, type);
        GeneralResponse <LikeResponseDto> generalResponse = new GeneralResponse<>(postsService.getLikes(itemId, type));
        log.debug("getLikes(): response = {}", generalResponse);
        log.info("getLikes(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/likes")
    public ResponseEntity<GeneralResponse<LikeResponseDto>> setLike(@RequestBody LikeRequest request) {

        log.info("setLike(): start():");
        log.debug("setLike(): requestBody = {}", request);
        GeneralResponse<LikeResponseDto> generalResponse = new GeneralResponse<>(postsService.setLikes(request));
        log.debug("setLike(): response = {}", generalResponse);
        log.info("setLike(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @DeleteMapping("/likes")
    public ResponseEntity<GeneralResponse<Map<String, String>>> deleteLike(@RequestParam(value = "item_id") int itemId,
                                                                           @RequestParam(value = "type") String type) {
        log.info("deleteLike(): start():");
        log.debug("deleteLike(): itemId = {}, type = {}", itemId, type);
        GeneralResponse<Map<String, String>> generalResponse = new GeneralResponse<>(postsService.deleteLike(itemId, type));
        log.debug("deleteLike(): response = {}", generalResponse);
        log.info("deleteLike(): finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
