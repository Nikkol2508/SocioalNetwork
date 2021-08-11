package application.controllers;

import application.models.Tag;
import application.requests.TagRequest;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import application.service.PostsService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TagController {

  private final PostsService postsService;

  @GetMapping("/tags")
  public ResponseEntity<GeneralListResponse<Tag>> getTags(
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "itemPerPage", required = false) Integer itemPerPage) {
    return ResponseEntity.ok(postsService.getTags());
  }

  @PostMapping("/tags")
  public ResponseEntity<GeneralResponse<Tag>> setTag(@RequestBody TagRequest request) {
    return ResponseEntity.ok(postsService.setTag(request));
  }

  @DeleteMapping("/tags")
  public ResponseEntity<GeneralResponse<HashMap<String, String>>> deleteTag(@RequestParam int id) {
    return ResponseEntity.ok(postsService.deleteTag(id));
  }
}
