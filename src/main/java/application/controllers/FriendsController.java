package application.controllers;

import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.FriendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class FriendsController {
    private final FriendsService friendService;

    @GetMapping("/friends")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriends(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(friendService.getUserFriends(), offset, itemPerPage));
    }

    @GetMapping("/friends/request")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRequest(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(friendService.getUserFriendsRequest(), offset, itemPerPage));
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRecommendations(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(friendService
                .getUserFriendsRecommendations(), offset, itemPerPage));
    }

    @PostMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> addFriendForId(@PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(friendService.addFriendForId(id)));
    }

    @DeleteMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> deleteFriendForId(@PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(friendService.deleteFriendForId(id)));
    }
}
