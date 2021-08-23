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
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriends() {
        return ResponseEntity.ok(friendService.getUserFriends());
    }

    @GetMapping("/friends/request")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRequest() {
        return ResponseEntity.ok(friendService.getUserFriendsRequest());
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRecommendations() {
        return ResponseEntity.ok(friendService.getUserFriendsRecommendations());
    }

    @PostMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> addFriendForId(@PathVariable int id) {
        return ResponseEntity.ok(friendService.addFriendForId(id));
    }

    @DeleteMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> deleteFriendForId(@PathVariable int id) {
        return ResponseEntity.ok(friendService.deleteFriendForId(id));
    }
}
