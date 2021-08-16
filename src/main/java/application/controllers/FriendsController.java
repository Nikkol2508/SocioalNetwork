package application.controllers;

import application.models.Person;
import application.models.PersonDto;
import application.models.responses.GeneralListResponse;
import application.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendService;

    @GetMapping("/friends")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriends() {
        return ResponseEntity.ok(friendService.getUserFriends(2));
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
    public ResponseEntity<GeneralResponse<MessageRequestDto>> addFriendForId(@PathVariable int id) {
        return ResponseEntity.ok(friendService.addFriendForId(id));
    }

    @DeleteMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> deleteFriendForId(@PathVariable int id) {
        return ResponseEntity.ok(friendService.deleteFriendForId(id));
    }
}
