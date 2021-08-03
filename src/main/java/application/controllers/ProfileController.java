package application.controllers;

import application.models.PersonDto;
import application.models.PostDto;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<PersonDto>> getProfile() {
        return ResponseEntity.ok(profileService.getPerson());
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<GeneralListResponse<PostDto>> getUserPosts(@RequestParam int id) {
        return ResponseEntity.ok(profileService.getUserPosts(id));
    }
}
