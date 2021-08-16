package application.controllers;

import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public ResponseEntity<GeneralListResponse<PersonDto>> getPersons(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Long ageFrom,
            @RequestParam(value = "age_to", required = false) Long ageTo,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "city", required = false) String city) {
        return ResponseEntity.ok(profileService.getPersons(firstName, lastName, ageFrom, ageTo, country, city));
    }

}
