package application.controllers;

import application.models.Tag;
import application.models.requests.TagRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TagController {

    private final PostsService postsService;

    @GetMapping("/tags")
    public ResponseEntity<GeneralListResponse<Tag>> getTags(
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "offset", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(postsService.getTags(), offset, itemPerPage));
    }

    @PostMapping("/tags")
    public ResponseEntity<GeneralResponse<Tag>> setTag(@RequestBody TagRequest request) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.setTag(request)));
    }

    @DeleteMapping("/tags")
    public ResponseEntity<GeneralResponse<HashMap<String, String>>> deleteTag(@RequestParam int id) {

        return ResponseEntity.ok(new GeneralResponse<>(postsService.deleteTag(id)));
    }
}
