package application.controllers;

import application.models.PersonDto;
import application.models.PostDto;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<PersonDto>> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<PersonDto>> getPerson(@PathVariable int id) {
        return ResponseEntity.ok(profileService.getPerson(id));
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<GeneralListResponse<PostDto>> getWall(@PathVariable int id) {
        return ResponseEntity.ok(profileService.getWall(id));
    }
}
