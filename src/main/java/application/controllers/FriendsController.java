package application.controllers;

import application.models.Person;
import application.responses.GeneralListResponse;
import application.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendService;

    @GetMapping("/friends")
    public ResponseEntity<GeneralListResponse<Person>> getUserFriends() {
        return ResponseEntity.ok(friendService.getUserFriends());
    }

    @GetMapping("/friends/request")
    public ResponseEntity<GeneralListResponse<Person>> getUserFriendsRequest() {
        return ResponseEntity.ok(friendService.getUserFriendsRequest());
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<GeneralListResponse<Person>> getUserFriendsRecommendations() {
        return ResponseEntity.ok(friendService.getUserFriendsRecommendations());
    }
}
