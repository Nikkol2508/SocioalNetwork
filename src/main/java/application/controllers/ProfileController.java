package application.controllers;

import application.models.PersonDto;
import application.responses.GeneralResponse;
import application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<PersonDto>> getProfile() {
        return ResponseEntity.ok(profileService.getPerson());
    }
}
