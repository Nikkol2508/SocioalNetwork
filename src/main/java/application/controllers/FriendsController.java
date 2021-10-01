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

        log.info("getUserFriends(): start():");
        log.debug("getUserFriends(): name= {}, offset = {}, itemPerPage = {}", name, offset, itemPerPage);
        GeneralListResponse<PersonDto> generalListResponse = new GeneralListResponse<>(friendService.getUserFriends(), offset, itemPerPage);
        log.debug("getUserFriends(): responseList = {}", generalListResponse);
        log.info("getUserFriends(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @GetMapping("/friends/request")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRequest(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getUserFriendsRequest(): start():");
        log.debug("getUserFriendsRequest(): name = {}, offset = {}, itemPerPage = {}", name, offset, itemPerPage);
        GeneralListResponse<PersonDto> generalListResponse = new GeneralListResponse<>(friendService.getUserFriendsRequest(), offset, itemPerPage);
        log.debug("getUserFriendsRequest(): responseList = {}", generalListResponse);
        log.info("getUserFriendsRequest(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<GeneralListResponse<PersonDto>> getUserFriendsRecommendations(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getUserFriendsRecommendations(): start():");
        GeneralListResponse<PersonDto> generalListResponse = new GeneralListResponse<>(friendService
                .getUserFriendsRecommendations(), offset, itemPerPage);
        log.debug("getUserFriendsRecommendations(): responseList = {}", generalListResponse);
        log.info("getUserFriendsRecommendations(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @PostMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> addFriendForId(@PathVariable int id) {

        log.info("addFriendForId(): start():");
        log.debug("addFriendForId(): friendId = {}", id);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(friendService.addFriendForId(id));
        log.debug("addFriendForId(): response = {}", generalResponse);
        log.info("addFriendForId(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @DeleteMapping("friends/{id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> deleteFriendForId(@PathVariable int id) {

        log.info("deleteFriendForId(): start():");
        log.debug("deleteFriendForId(): friendId = {}", id);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(friendService.deleteFriendForId(id));
        log.debug("deleteFriendForId(): response = {}", generalResponse);
        log.info("deleteFriendForId(): finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
