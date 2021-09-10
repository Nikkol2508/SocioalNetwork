package application.controllers;

import application.models.dto.PostDto;
import application.models.responses.GeneralListResponse;
import application.service.FeedsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FeedsController {

    private final FeedsService feedsService;

    @GetMapping("/feeds")
    public ResponseEntity<GeneralListResponse<PostDto>> getFeed(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "5", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(feedsService.getFeed(), offset, itemPerPage));
    }
}
