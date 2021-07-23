package application.controllers;

import application.models.Post;
import application.responses.GeneralListResponse;
import application.service.FeedsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FeedsController {
  private final FeedsService feedsService;

  @GetMapping("/feeds")
  public ResponseEntity<GeneralListResponse<Post>> getFeed(){
      return ResponseEntity.ok(feedsService.getFeed());
  }
}
