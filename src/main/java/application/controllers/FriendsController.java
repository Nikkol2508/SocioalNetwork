package application.controllers;

import application.models.PersonDto;
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
}
